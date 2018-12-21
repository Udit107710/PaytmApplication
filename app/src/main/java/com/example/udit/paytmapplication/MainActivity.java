package com.example.udit.paytmapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.print.PageRange;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private String txnAmount;
    //public String custID;
    //public String orderID;
    public String mobileNo;
    //public  String CALLBACK_URL = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=";
    private HashMap<String, String> paramMap = new HashMap<String, String>();
    /*public static final String M_ID = "NoneSt61872611077296";
    public static final String CHANNEL_ID = "WAP";
    public static final String INDUSTRY_TYPE_ID = "Retail";
    public static final String WEBSITE = "WEBSTAGING";*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        EditText amount = (EditText) findViewById(R.id.transaction_amount);
        txnAmount = amount.getText().toString();

        EditText mobile_no = (EditText) findViewById(R.id.cust_mobile_no);
        mobileNo = mobile_no.getText().toString();

    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    public void onStartTransaction(View view) {
        if(!mobileNo.isEmpty() && !txnAmount.isEmpty())
        {
            //custID = mobileNo + "ACM";
            //Long ts = System.currentTimeMillis();
            //orderID = ts.toString();
            //orderID = orderID + mobileNo;
            //CALLBACK_URL = CALLBACK_URL + orderID;
            generateChecksumHash();
        }

        else Toast.makeText(getBaseContext(), "Enter details", Toast.LENGTH_SHORT);
    }

    private void generateChecksumHash()
    {

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        Api apiService = retrofit.create(Api.class);

        final Paytm paytm = new Paytm(
                Constants.M_ID,
                Constants.CHANNEL_ID,
                txnAmount,
                Constants.WEBSITE,
                Constants.CALLBACK_URL ,
                Constants.INDUSTRY_TYPE_ID);

        Call<Checksum> call = apiService.getChecksum(
                paytm.getmId(),
                paytm.getOrderId(),
                paytm.getCustId(),
                paytm.getChannelId(),
                paytm.getTxnAmount(),
                paytm.getWebsite(),
                paytm.getCallBackUrl(),
                paytm.getIndustryTypeId());

        call.enqueue(new Callback<Checksum>() {
            @Override
            public void onResponse(Call<Checksum> call, Response<Checksum> response) {
                String checkSumHash = response.body().getChecksumHash();
                Log.d("IMPORTANT CheckSumHash",checkSumHash);
                Log.d("IMPORTANT Payt_status", response.body().getPaytStatus());
                Log.d("IMPORTANT OrderId",response.body().getOrderId());
                Toast.makeText(getBaseContext(),"CHECKSUM GENERATED",Toast.LENGTH_LONG).show();

                initializePayment(checkSumHash, paytm);
            }

            @Override
            public void onFailure(Call<Checksum> call, Throwable t) {
                Log.d("CheckSumHash","Message:" + t.getMessage());
                Toast.makeText(getBaseContext(),"Checksum didn't generate",Toast.LENGTH_LONG).show();
            }
        });


    }
    private void initializePayment(String checksumHash, Paytm paytm)
    {

        PaytmPGService Service = PaytmPGService.getStagingService();

        paramMap.put("MID", paytm.getmId());
        paramMap.put("ORDER_ID", paytm.getOrderId());
        paramMap.put("CUST_ID", paytm.getCustId());
        paramMap.put("MOBILE_NO", mobileNo);
        paramMap.put("CHANNEL_ID", paytm.getChannelId());
        paramMap.put("TXN_AMOUNT", txnAmount);
        paramMap.put("WEBSITE", paytm.getCustId());
        paramMap.put("CALLBACK_URL", paytm.getCallBackUrl());
        paramMap.put("INDUSTRY_TYPE_ID", paytm.getIndustryTypeId());
        paramMap.put("CHECKSUMHASH", checksumHash);

        PaytmOrder order = new PaytmOrder(paramMap);

        Service.initialize(order, null);
        Service.startPaymentTransaction(this, true, true, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(Bundle inResponse) {
                Log.d("LOG", "Payment Transaction is successful " + inResponse);
                Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void networkNotAvailable() {

            }

            @Override
            public void clientAuthenticationFailed(String inErrorMessage) {

            }

            @Override
            public void someUIErrorOccurred(String inErrorMessage) {

            }

            @Override
            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {

            }

            @Override
            public void onBackPressedCancelTransaction() {

            }

            @Override
            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {

            }
        });

    }

}
