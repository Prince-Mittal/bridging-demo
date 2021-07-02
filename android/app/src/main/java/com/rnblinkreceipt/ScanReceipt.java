package com.rnblinkreceipt;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.microblink.CameraScanActivity;
import com.microblink.FrameCharacteristics;
import com.microblink.Media;
import com.microblink.ScanOptions;
import com.microblink.core.FloatType;
import com.microblink.core.Retailer;
import com.microblink.core.ScanResults;
import com.microblink.core.StringType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class ScanReceipt extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;
    private static final int SCAN_RECEIPT_REQUEST = 1;
    private Callback successCallback = null;
    private Callback errorCallback  = null;

    private final JSONObject getStringTypeObject(StringType key){
        JSONObject data = new JSONObject();
        try {
            if(key != null) {
                data.put("value", key.value());
                data.put("confidence", key.confidence());
            }else{
                return null;
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        return data;
    }

    private final JSONObject getFloatTypeObject(FloatType key){
        JSONObject data = new JSONObject();
        try {
            if(key != null){
                data.put("value", key.value());
                data.put("confidence", key.confidence());
            }else{
                return null;
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        return data;
    }

    private final String jsonWrapper(ScanResults scanData){
        String response = null;
        JSONObject data = new JSONObject();
        try {
            if(scanData != null && scanData.products() != null && scanData.products().size() > 0){
                data.put("isDuplicate", scanData.duplicate());
                data.put("isFraudulent", scanData.fraudulent());
                data.put("receiptDate", getStringTypeObject(scanData.receiptDate()));
                data.put("receiptTime", getStringTypeObject(scanData.receiptTime()));
                data.put("retailerId", scanData.retailerId());
                data.put("total", getFloatTypeObject(scanData.total()));
                data.put("subtotal", getFloatTypeObject(scanData.subtotal()));
                data.put("taxes", getFloatTypeObject(scanData.taxes()));
                data.put("storeNumber", getStringTypeObject(scanData.storeNumber()));
                data.put("merchantName", getStringTypeObject(scanData.merchantName()));
                data.put("storeAddress", getStringTypeObject(scanData.storeAddress()));
                data.put("storeCity", getStringTypeObject(scanData.storeCity()));
                data.put("storeState", getStringTypeObject(scanData.storeState()));
                data.put("storeZip", getStringTypeObject(scanData.storeZip()));
                data.put("storeCountry", getStringTypeObject(scanData.storeCountry()));
                data.put("storePhone", getStringTypeObject(scanData.storePhone()));
                data.put("transactionId", getStringTypeObject(scanData.transactionId()));
                data.put("receiptDateTime", scanData.receiptDateTime() != null ? scanData.receiptDateTime().toString() : null);
                data.put("cashierId", getStringTypeObject(scanData.cashierId()));
                if (scanData.products() != null && scanData.products().size() > 0) {
                    JSONArray productArray = new JSONArray();
                    for (int i = 0; i < scanData.products().size(); i++) {
                        JSONObject product = new JSONObject();
                        product.put("productNumber", getStringTypeObject(scanData.products().get(i).productNumber()));
                        product.put("productDescription", getStringTypeObject(scanData.products().get(i).description()));
                        product.put("quantity", getFloatTypeObject(scanData.products().get(i).quantity()));
                        product.put("unitPrice", getFloatTypeObject(scanData.products().get(i).unitPrice()));
                        product.put("unitOfMeasure", getStringTypeObject(scanData.products().get(i).unitOfMeasure()));
                        product.put("totalPrice", getFloatTypeObject(scanData.products().get(i).totalPrice()));
                        product.put("upc", scanData.products().get(i).upc());
                        productArray.put(product);
                    }
                    data.put("products", productArray);
                }
                if(scanData.paymentMethods() != null && scanData.paymentMethods().size() > 0){
                    JSONArray paymentMethods = new JSONArray();
                    for (int i = 0; i < scanData.paymentMethods().size(); i++) {
                        JSONObject payMethod = new JSONObject();
                        payMethod.put("amount", getFloatTypeObject(scanData.paymentMethods().get(i).amount()));
                        payMethod.put("method", getStringTypeObject(scanData.paymentMethods().get(i).paymentMethod()));
                        payMethod.put("cardType", getStringTypeObject(scanData.paymentMethods().get(i).cardType()));
                        payMethod.put("cardIssuer", getStringTypeObject(scanData.paymentMethods().get(i).cardType()));
                        paymentMethods.put(payMethod);
                    }
                    data.put("paymentMethods", paymentMethods);
                }
                response = data.toString();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return response;
    }

    private final ActivityEventListener activityEventListener = new BaseActivityEventListener(){
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            try {
                if (requestCode == SCAN_RECEIPT_REQUEST && resultCode == Activity.RESULT_OK) {
                    ScanResults brScanResults = data.getParcelableExtra(CameraScanActivity.DATA_EXTRA);

                    Media media = data.getParcelableExtra(CameraScanActivity.MEDIA_EXTRA);
                    List<File> receiptImage = media.items();
                    String imageArr = "";
                    try {
                        for(int i=0;i<receiptImage.size();i++){
                            imageArr += receiptImage.get(i).toString();
                            if(i!=receiptImage.size()-1){
                                imageArr += " ";
                            }
                        }
                        String wrappedData = jsonWrapper(brScanResults);
                        if(wrappedData == null){
                            errorCallback.invoke("INVALID_RECEIPT");
                        }else {
                            successCallback.invoke(wrappedData, imageArr);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        errorCallback.invoke(e.getMessage());
                    }
                } else if (requestCode == SCAN_RECEIPT_REQUEST && resultCode == Activity.RESULT_CANCELED && data != null) {
                    errorCallback.invoke(data.getStringExtra("error"));
                } else if (requestCode == SCAN_RECEIPT_REQUEST && resultCode == Activity.RESULT_CANCELED) {
                    errorCallback.invoke("RECEIPT_SCAN_CANCELLED");
                }
            } catch(Exception e){
                errorCallback.invoke(e.getMessage());
            }
        }
    };

    ScanReceipt(ReactApplicationContext context) {
        super(context);
        reactContext = context;
        reactContext.addActivityEventListener(activityEventListener);
    }
    @Override
    public String getName() {
        return "ScanReceipt";
    }
    @ReactMethod
    public void scan(Callback success, Callback error){
        try{
            successCallback = success;
            errorCallback = error;
            ScanOptions scanOptions = ScanOptions.newBuilder()
                    .retailer( Retailer.UNKNOWN )
                    .detectDuplicates(true)
                    .frameCharacteristics( FrameCharacteristics.newBuilder()
                            .storeFrames( true )
                            .compressionQuality( 100 )
                            .externalStorage( false ) .build() )
                    .logoDetection( true )
                    .build();

            Bundle bundle = new Bundle();

            bundle.putParcelable( CameraScanActivity.SCAN_OPTIONS_EXTRA, scanOptions );
            Activity currentActivity = getCurrentActivity();
            if(currentActivity == null)
                return;
            Intent intent = new Intent( reactContext, CameraScanActivity.class )
                    .putExtra( CameraScanActivity.BUNDLE_EXTRA, bundle );

            currentActivity.startActivityForResult( intent, SCAN_RECEIPT_REQUEST );
        }catch(Exception e){
            errorCallback.invoke(e.getMessage());
        }
    }
}