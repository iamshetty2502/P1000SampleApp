package com.sil.y1000pos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.sil.silp1000sdk.P1000CallBacks;
import com.sil.silp1000sdk.P1000Manager;
import com.sil.silp1000sdk.POJO.P1000Request;
import com.sil.silp1000sdk.TransactionType;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        call();
    }
    void call() {
        P1000Manager p1000Manager = P1000Manager.getInstance(this, "LICENSE-KEY");
        P1000Request p1000Request = new P1000Request();
        p1000Request.setUsername("USERNAME");
        p1000Request.setPassword("PASSWORD");
        p1000Request.setRefCompany("COMPANYNAME");
        p1000Request.setMid("MERCHANMT_ID");
        p1000Request.setTid("TERMINAL_ID");
        p1000Request.setTransactionId("TRANSACTION_ID"); //p1000Manager.getTransactionId()
        p1000Request.setImei("IMEI");
        p1000Request.setImsi("IMSI");
        p1000Request.setTxn_amount("AMOUNT");
        p1000Request.setRequestCode(TransactionType.INQUIRY);
        p1000Manager.execute(p1000Request, new P1000CallBacks() {
            @Override
            public void successCallback(JSONObject responseSuccess) {
                Log.d(TAG, "successCallback: " + responseSuccess);
            }

            @Override
            public void progressCallback(String string) {
                Log.d(TAG, "progressCallback: " + string);
            }

            @Override
            public void failureCallback(JSONObject responseSuccess) {
                Log.d(TAG, "failureCallback: " + responseSuccess);
            }
        });
    }
}