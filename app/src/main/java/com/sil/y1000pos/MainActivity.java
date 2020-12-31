package com.sil.y1000pos;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.basewin.services.ServiceManager;
import com.sil.silp1000sdk.P1000CallBacks;
import com.sil.silp1000sdk.P1000Manager;
import com.sil.silp1000sdk.P1000VoidCallBacks;
import com.sil.silp1000sdk.POJO.P1000Request;
import com.sil.silp1000sdk.TransactionType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog progressdialog;
    Button balanceEnquiryBtn, saleByCardBtn, cashWithdrawalBtn, voidTransactionBtn, printBtn;
    String printMessage = "Hello World";
    P1000Manager p1000Manager;
    private static final String TAG = "MainActivity";
    boolean canBeVoided = false;
    String transactionId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        balanceEnquiryBtn = findViewById(R.id.balanceEnquiry);
        saleByCardBtn = findViewById(R.id.saleByCard);
        cashWithdrawalBtn = findViewById(R.id.cashWithdrawal);
        voidTransactionBtn = findViewById(R.id.voidTransaction);
        printBtn = findViewById(R.id.printText);

        progressdialog = new ProgressDialog(this);

        balanceEnquiryBtn.setOnClickListener(this);
        saleByCardBtn.setOnClickListener(this);
        cashWithdrawalBtn.setOnClickListener(this);
        voidTransactionBtn.setOnClickListener(this);
        printBtn.setOnClickListener(this);

    }

    void call(com.sil.silp1000sdk.TransactionType transactionType, String amount) {

        if (p1000Manager == null) {
            if (progressdialog != null) {
                progressdialog.setMessage("Initialiasing Transaction");
                if (!progressdialog.isShowing()) {
                    progressdialog.show();
                }
            }

            p1000Manager = P1000Manager.getInstance(this, "LICENSE-KEY");
        }
        P1000Request p1000Request = new P1000Request();
        p1000Request.setUsername("USERNAME");
        p1000Request.setPassword("PASSWORD");
        p1000Request.setRefCompany("COMPANYNAME");
        p1000Request.setMid("MERCHANT_ID");
        p1000Request.setTid("TERMINAL_ID");
        transactionId = p1000Manager.getTransactionId(); // You can use your own 12 digit Alphanumeric RRN
        p1000Request.setTransactionId(transactionId);
        p1000Request.setImei("IMEI");
        p1000Request.setImsi("IMSI");
        p1000Request.setTxn_amount(amount);
        p1000Request.setRequestCode(transactionType);
        p1000Manager.execute(p1000Request, new P1000CallBacks() {
            @Override
            public void successCallback(JSONObject responseSuccess) {
                Log.d(TAG, "successCallback: " + responseSuccess);
                printMessage = getReceiptText(responseSuccess);
                closeProgressDialog("TRANSACTION SUCCESS");
            }

            @Override
            public void progressCallback(String string) {
                Log.d(TAG, "progressCallback: " + string);
                updateProcessDialog(string);
            }

            @Override
            public void failureCallback(JSONObject responseFailure) {
                Log.d(TAG, "failureCallback: " + responseFailure);
                printMessage = getReceiptText(responseFailure);
                closeProgressDialog("TRANSACTION FAILED");
            }
        });
    }

    private void closeProgressDialog(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressdialog != null && progressdialog.isShowing()) {
                    progressdialog.dismiss();
                }
                showToast(message);
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateProcessDialog(final String string) {
        if (string != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (progressdialog != null) {
                        progressdialog.setMessage(string);
                        if (!progressdialog.isShowing()) {
                            progressdialog.show();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.balanceEnquiry:
                canBeVoided = false;
                call(TransactionType.INQUIRY, "1");
                break;
            case R.id.saleByCard:
                canBeVoided = true;
                call(TransactionType.DEBIT, "10");
                break;
            case R.id.cashWithdrawal:
                canBeVoided = false;
                call(TransactionType.WITHDRAWAL, "100");
                break;
            case R.id.voidTransaction:
                voidTransaction();
                break;
            case R.id.printText:
                printText();
                break;
        }
    }

    private void voidTransaction() {
        if (canBeVoided) {
            if (transactionId != null) {
                P1000Request p1000Request = new P1000Request();
                p1000Request.setUsername("USERNAME");
                p1000Request.setTxn_amount("TRANSACTION_AMOUNT");
                p1000Request.setImei("IMEI");
                p1000Request.setImsi("IMSI");
                p1000Request.setMid("MERCHANT_ID");
                p1000Request.setTid("TERMINAL_ID");
                p1000Request.setTransactionId(transactionId); // Transaction Id of Sale By Card
                p1000Request.setRefCompany("COMPANYNAME");
                p1000Request.setPassword("PASSWORD");
                if (p1000Manager == null) {
                    if (progressdialog != null) {
                        progressdialog.setMessage("Initialising Transaction");
                        if (!progressdialog.isShowing()) {
                            progressdialog.show();
                        }
                    }

                    p1000Manager = P1000Manager.getInstance(this, "9F1A0203569F3602000E5F3401019F37");
                }
                p1000Manager.voidTransaction(p1000Request, new P1000VoidCallBacks() {
                    @Override
                    public void successCallback(JSONObject responseSuccess) {
                        Log.d(TAG, "voidTransaction successCallback : " + responseSuccess);
                        closeProgressDialog("VOID SUCCESS");
                    }

                    @Override
                    public void progressCallback(String message) {
                        Log.d(TAG, "voidTransaction progressCallback: " + message);
                        updateProcessDialog(message);
                    }

                    @Override
                    public void failureCallback(JSONObject responseFailure) {
                        Log.d(TAG, "voidTransaction failureCallback: " + responseFailure);
                        closeProgressDialog("VOID FAILED");
                    }
                });

            }
        } else {
            showToast("Only Sale By Card Transaction can be Voided.");
        }
    }

    private void printText() {
        printString(printMessage);
    }


    public void printString(String str) {
        JSONObject printJson = new JSONObject();
        JSONArray printTest = new JSONArray();
        // add text printer
        try {

            int grayScale = 1000;

            // add picture
            JSONObject json11 = new JSONObject();
            json11.put("content-type", "txt");
            json11.put("position", "left");
            json11.put("bold", "1");            //0:no bold, 1: bold
            json11.put("size", "1");

            ServiceManager.getInstence().getPrinter().setLineSpace(Integer.valueOf("1"));//set lineSpace
            ServiceManager.getInstence().getPrinter().setPrintFont("CutiveMono.ttf");
            printTest.put(json11);

            printJson.put("spos", printTest);
            ServiceManager.getInstence().getPrinter().setFontSize(19);
            ServiceManager.getInstence().getPrinter().setPrintGray(grayScale);
            ServiceManager.getInstence().getPrinter().printText(str);


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getReceiptText(JSONObject jsonObject) {
        ReceiptBuilder rb = new ReceiptBuilder(ReceiptBuilder.fullLen);
        getKeyValue(rb, jsonObject);
        return (rb.build());
    }

    private void getKeyValue(ReceiptBuilder rb, JSONObject jsonObject) {
        // Get keys from json
        String value = "";
        Iterator<String> panelKeys = jsonObject.keys();
        while (panelKeys.hasNext()) {
            String key = panelKeys.next();
            try {
                Object panel = jsonObject.get(key);
                if (panel instanceof JSONObject) {
                    getKeyValue(rb, (JSONObject) panel);
                } else if (panel instanceof String) {
                    try {
                        JSONObject jsonObject1 = new JSONObject((String) panel);
                        getKeyValue(rb, jsonObject1);
                    } catch (Exception e) {
                        value = key + " " + (String) panel + "\n";
                    }
                } else if (panel instanceof Double) {
                    value = key + " " + (Double) panel + "\n";
                } else if (panel instanceof Integer) {
                    value = key + " " + (Integer) panel + "\n";
                } else if (panel instanceof Boolean) {
                    value = key + " " + (Boolean) panel + "\n";
                }

                rb.appendCenter(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}