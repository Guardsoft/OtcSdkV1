package com.centinel.appclienteotc2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.otc.sdk.pos.flows.ConfSdk;
import com.otc.sdk.pos.flows.sources.config.CustomError;
import com.otc.sdk.pos.flows.sources.config.InitializeResponseHandler;
import com.otc.sdk.pos.flows.sources.config.QueryResponseHandler;
import com.otc.sdk.pos.flows.sources.server.models.request.authorize.Order;
import com.otc.sdk.pos.flows.sources.server.models.response.authorize.AuthorizeResponse;
import com.otc.sdk.pos.flows.sources.server.models.response.initialize.InitializeResponse;
import com.otc.sdk.pos.flows.sources.server.models.response.retrieve.RetrieveResponse;
import com.otc.sdk.pos.flows.sources.server.rest.ProcessInitializeCallback;
import com.otc.sdk.pos.flows.sources.server.rest.ProcessRetrieveListCallback;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private LinearLayout layoutProgress;

    ConfSdk confsdk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        layoutProgress = findViewById(R.id.layout_progress);

        confsdk = new ConfSdk();

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

        ConfSdk.endpoint = "https://culqimpos.quiputech.com/";
        ConfSdk.tenant = "culqi";

        ConfSdk.username = "integracion@otcperu.com";
        ConfSdk.password = "Peru2019$$";

        //llave master
        ConfSdk.keyTmk = 1; // default
        //llaves
        ConfSdk.keyData = 10;
        ConfSdk.keyPin = 10;
        ConfSdk.keyMac = 10;

        layoutProgress.setVisibility(View.VISIBLE);

        // INITIALIZE -----------------------------------------------------------------------------
        ProcessInitializeCallback callback = new ProcessInitializeCallback();
        callback.initialization(this, new InitializeResponseHandler() {
            @Override
            public void onSuccess(InitializeResponse response) {
                layoutProgress.setVisibility(View.GONE);
                Log.i(TAG, "onSuccess: " + response);
                ShowMessage(response.toString());

            }

            @Override
            public void onError(CustomError error) {
                layoutProgress.setVisibility(View.GONE);
                Log.i(TAG, "onError: " + error.getMessage());
                Log.i(TAG, "onError: " + error.getStatusCode());
            }
        });

    }

    public void query(View view) {

        layoutProgress.setVisibility(View.VISIBLE);

        ProcessRetrieveListCallback callback = new ProcessRetrieveListCallback();
        callback.retrieveList(this, 1 , 100, new QueryResponseHandler() {
            @Override
            public void onSuccess(RetrieveResponse response) {

                layoutProgress.setVisibility(View.GONE);
                Log.i(TAG, "onSuccess: " + response);
                ShowMessage(response.toString());

            }

            @Override
            public void onError(CustomError error) {

                layoutProgress.setVisibility(View.GONE);
                Log.i(TAG, "error: " + error);
                ShowMessage(error.toString());
            }
        });

    }

    public void authorization(View view) {

        Order order = new Order();
        order.setPurchaseNumber("20200710101");
        order.setAmount(35.00);
        order.setCurrency("PEN");
        order.setCountable(true);

        confsdk.processAuthorize(this, order);
    }

    public void voidOrder(View view) {

        Order order = new Order();
        order.setPurchaseNumber("20200710101");
        order.setAmount(35.00);
        order.setCurrency("PEN");
        order.setCountable(true);

        confsdk.processVoidOrder(this, order);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == ConfSdk.ACTIVITY_SDK_QUERY) {
            if (resultCode == RESULT_OK && data != null) {
                RetrieveResponse resultData = (RetrieveResponse) data.getExtras().getParcelable(ConfSdk.SUCCESS);
                ShowMessage(resultData.toString());
            }
            if (resultCode == RESULT_CANCELED && data != null) {
                CustomError resultData = (CustomError) data.getExtras().getParcelable(ConfSdk.ERROR);
                ShowMessage(resultData.toString());
            }
        }
    }


    private void ShowMessage(String msg) {

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("OTC - DEMO")
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setScroller(new Scroller(this));
        textView.setVerticalScrollBarEnabled(true);
        textView.setMovementMethod(new ScrollingMovementMethod());

    }


}
