package com.centinel.appclienteotc2;

import android.content.Intent;
import android.os.Bundle;

import com.otc.sdk.pax.a920.OtcApplication;
import com.otc.sdk.pos.flows.App;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.SwingCardActivity;
import com.otc.sdk.pos.flows.sources.config.CustomError;
import com.otc.sdk.pos.flows.sources.config.InitializeResponseHandler;
import com.otc.sdk.pos.flows.sources.server.models.request.AuthorizeRequest;
import com.otc.sdk.pos.flows.sources.server.models.response.initialize.InitializeResponse;
import com.otc.sdk.pos.flows.sources.server.rest.ProcessInitializeCallback;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private LinearLayout layoutProgress;
    int ACTIVITY_READ_CARD = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        layoutProgress = findViewById(R.id.layout_progress);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initialization(View view) {

        App.endpoint = "https://culqimpos.quiputech.com/";
        //App.tenant = "culqi";
        App.tenant = "cajatrujillo";

        App.username = "integracion@otcperu.com";
        App.password = "Peru2019$$";

        // recargar llaves
        App.initializeKeys = true;

        //llave master
        //App.keyTmk = 1; // default
        //llaves
        App.keyData = 10;
        App.keyPin = 10;
        App.keyMac = 10;

        layoutProgress.setVisibility(View.VISIBLE);

        // INITIALIZE -----------------------------------------------------------------------------
        ProcessInitializeCallback callback = new ProcessInitializeCallback();
        callback.initialization(this, new InitializeResponseHandler() {
            @Override
            public void onSuccess(InitializeResponse response) {
                layoutProgress.setVisibility(View.GONE);
                Log.i(TAG, "onSuccess: " + response);
            }

            @Override
            public void onError(CustomError error) {
                layoutProgress.setVisibility(View.GONE);
                Log.i(TAG, "onError: " + error.getMessage());
                Log.i(TAG, "onError: " + error.getStatusCode());
            }
        });

    }

    public void signature(){
        OtcApplication.getCrypted().getMacSignature("payload");
    }


    private void actionAuthorization(Intent data){

        String pan = data.getStringExtra("pan");
        String track2 = data.getStringExtra("track2");
        String pinBlock = data.getStringExtra("pinBlock");
        String type = data.getStringExtra("type");
        String pin = data.getStringExtra("pin");

        Log.i(TAG, "actionAuthorization: PAN " + pan);
        Log.i(TAG, "actionAuthorization: track2 " + track2);
        Log.i(TAG, "actionAuthorization: pinBlock " + pinBlock);
        Log.i(TAG, "actionAuthorization: type " + type);
        Log.i(TAG, "actionAuthorization: pin " + pin);


        AuthorizeRequest request = new AuthorizeRequest();

//        ProcessAuthorizeCallback authorizeCallback = new ProcessAuthorizeCallback();
//        authorizeCallback.authorization(this, request, new StringResponseHandler() {
//            @Override
//            public void onSuccess(String response) {
//
//                Log.i(TAG, "onSuccess: " + response);
//
//            }
//
//            @Override
//            public void onError(CustomError error) {
//
//                Log.i(TAG, "onError: " + error.getMessage());
//
//            }
//        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == ACTIVITY_READ_CARD) {

                if (resultCode == RESULT_OK) {
                    actionAuthorization(data);
                }

                if (resultCode == RESULT_CANCELED) {
                    Log.i(TAG, "onActivityResult: READ CARD CANCELADO -----------------------");
                }
            }
    }
}
