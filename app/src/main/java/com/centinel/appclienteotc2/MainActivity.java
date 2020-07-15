package com.centinel.appclienteotc2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.otc.sdk.pax.a920.OtcApplication;
import com.otc.sdk.pos.flows.ConfSdk;
import com.otc.sdk.pos.flows.sources.config.CustomError;
import com.otc.sdk.pos.flows.sources.config.InitializeResponseHandler;
import com.otc.sdk.pos.flows.sources.config.QueryResponseHandler;
import com.otc.sdk.pos.flows.sources.server.models.request.authorize.Order;
import com.otc.sdk.pos.flows.sources.server.models.response.authorize.AuthorizeResponse;
import com.otc.sdk.pos.flows.sources.server.models.response.initialize.InitializeResponse;
import com.otc.sdk.pos.flows.sources.server.models.response.retrieve.RetrieveResponse;
import com.otc.sdk.pos.flows.sources.server.rest.ProcessInitializeCallback;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import java.sql.Timestamp;

import static android.text.InputType.*;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private LinearLayout layoutProgress;

    ConfSdk confsdk;
    SharedPreferences prefsPax;
    long mPurchaseNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefsPax = getSharedPreferences("pax", Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        mPurchaseNumber = prefsPax.getLong("purchase_number", 0);

        if (mPurchaseNumber == 0) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            long time = timestamp.getTime()/1000;
            mPurchaseNumber = time;
        }else{
            mPurchaseNumber++;
        }

        SharedPreferences.Editor editor = prefsPax.edit();
        editor.putLong("purchase_number", mPurchaseNumber);
        editor.apply();

        layoutProgress = findViewById(R.id.layout_progress);

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

        confsdk = new ConfSdk();

        //ejemplo de firma 1
        OtcApplication.getCrypted().getMacSignature("texto a firmar");

        //ejemplo de firma 2
        int keyMac = 10;
        OtcApplication.getCrypted().getMacSignature("texto a firmar", keyMac);
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

        Log.i(TAG, "config endpoint => " + ConfSdk.endpoint);
        Log.i(TAG, "config tenant   => " + ConfSdk.tenant);
        Log.i(TAG, "config keyTmk   => " + ConfSdk.keyTmk);
        Log.i(TAG, "config keyData  => " + ConfSdk.keyData);
        Log.i(TAG, "config keyPin   => " + ConfSdk.keyPin);
        Log.i(TAG, "config keyMac   => " + ConfSdk.keyMac);

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
