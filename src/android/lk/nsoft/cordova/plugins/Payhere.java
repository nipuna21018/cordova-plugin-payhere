package lk.nsoft.cordova.plugins;

import org.apache.cordova.CordovaPlugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;

import org.apache.cordova.LOG;

/**
* Payhere imports.
*/
import lk.payhere.androidsdk.*;
import lk.payhere.androidsdk.model.*;
import lk.payhere.androidsdk.util.*;

public class Payhere extends CordovaPlugin {

    private final static int PAYHERE_REQUEST = 111010;
    protected CallbackContext callbackContext;
    private static final String TAG = "PAYHERE_PLUGIN";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("checkout")) {
            String message = args.getString(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    checkout(message, callbackContext);
                    callbackContext.success(); // Thread-safe.
                }
            });

            return true;
        }
        if (action.equals("preApprove")) {
            String message = args.getString(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    preApprove(message, callbackContext);
                    callbackContext.success(); // Thread-safe.
                }
            });
            return true;
        }
        return false;
    }

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    private void checkout(String message, CallbackContext callbackContext) {
        LOG.d(TAG, message);
        this.callbackContext = callbackContext;
        InitRequest req = new InitRequest();
        boolean isSandboxEnabled = false;
        try {
            JSONObject data = new JSONObject(message);

            if(data.has("sandboxEnabled")) {
                isSandboxEnabled = data.getBoolean("sandboxEnabled");
            }

            req.setMerchantId(data.getString("merchantId")); // Your Merchant ID
            req.setAmount(data.getDouble("amount")); // Amount which the customer should pay
            req.setCurrency(data.getString("currency")); // Currency
            req.setOrderId(data.getString("orderId")); // Unique ID for your payment transaction
            req.setItemsDescription(data.getString("itemsDescription")); // Item title or Order/Invoice number
            req.setCustom1(data.optString("custom1"));
            req.setCustom2(data.optString("custom2"));
            
            // notify url
            if(data.has('notifyURL')) {
                req.setNotifyUrl(data.getString("notifyURL"));
            }

            // customer details
            if(data.has("customer")) {
                req.getCustomer().setFirstName(data.getJSONObject("customer").optString("firstName"));
                req.getCustomer().setLastName(data.getJSONObject("customer").optString("lastName"));
                req.getCustomer().setEmail(data.getJSONObject("customer").optString("email"));
                req.getCustomer().setPhone(data.getJSONObject("customer").optString("phone"));
            }

            // billing details
            if(data.has("billing")) {
                req.getCustomer().getAddress().setAddress(data.getJSONObject("billing").optString("address"));
                req.getCustomer().getAddress().setCity(data.getJSONObject("billing").optString("city"));
                req.getCustomer().getAddress().setCountry(data.getJSONObject("billing").optString("country"));
            }

            //shipping details
            if(data.has("shipping")) {
                req.getCustomer().getDeliveryAddress().setAddress(data.getJSONObject("shipping").optString("address"));
                req.getCustomer().getDeliveryAddress().setCity(data.getJSONObject("shipping").optString("city"));
                req.getCustomer().getDeliveryAddress().setCountry(data.getJSONObject("shipping").optString("country"));
            }

            if(data.has("items")) {
                JSONArray items = data.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    req.getItems().add(new Item(item.getString("id"), item.getString("name"), item.getInt("quantity"), item.getDouble("amount")));
                }
            }

        } catch (JSONException $je) {
            callbackContext.error("Invalid data!");
        }
        Intent intent = new Intent(cordova.getActivity(), PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        PHConfigs.setBaseUrl(isSandboxEnabled ? PHConfigs.SANDBOX_URL : PHConfigs.LIVE_URL);
        if (this.cordova != null) {
            this.cordova.startActivityForResult((CordovaPlugin) this, intent, PAYHERE_REQUEST); //unique request ID like private final static int PAYHERE_REQUEST = 11010;
        }
    }
    private void preApprove(String message, CallbackContext callbackContext) {
        LOG.d(TAG, message);
        this.callbackContext = callbackContext;
        InitPreapprovalRequest req = new InitPreapprovalRequest();
        boolean isSandboxEnabled = false;
        try {
            JSONObject data = new JSONObject(message);

            if(data.has("sandboxEnabled")) {
              isSandboxEnabled = data.getBoolean("sandboxEnabled");
            }

            req.setMerchantId(data.getString("merchantId")); // Your Merchant ID

            req.setCurrency(data.getString("currency")); // Currency
            req.setOrderId(data.getString("orderId")); // Unique ID for your payment transaction
            req.setItemsDescription(data.getString("itemsDescription")); // Item title or Order/Invoice number
            req.setCustom1(data.optString("custom1"));
            req.setCustom2(data.optString("custom2"));

            // notify url
            if(data.has('notifyURL')) {
                req.setNotifyUrl(data.getString("notifyURL"));
            }
            
            // customer details
            if(data.has("customer")) {
                req.getCustomer().setFirstName(data.getJSONObject("customer").optString("firstName"));
                req.getCustomer().setLastName(data.getJSONObject("customer").optString("lastName"));
                req.getCustomer().setEmail(data.getJSONObject("customer").optString("email"));
                req.getCustomer().setPhone(data.getJSONObject("customer").optString("phone"));
            }

            // billing details
            if(data.has("billing")) {
                req.getCustomer().getAddress().setAddress(data.getJSONObject("billing").optString("address"));
                req.getCustomer().getAddress().setCity(data.getJSONObject("billing").optString("city"));
                req.getCustomer().getAddress().setCountry(data.getJSONObject("billing").optString("country"));
            }

            if(data.has("items")) {
                JSONArray items = data.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    req.getItems().add(new Item(item.getString("id"), item.getString("name"), item.getInt("quantity"), item.getDouble("amount")));
                }
            }

        } catch (JSONException $je) {
            callbackContext.error(getFormattedResponse("error",-100,"Invalid data provided",null).toString());
        }
        Intent intent = new Intent(cordova.getActivity(), PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        PHConfigs.setBaseUrl(isSandboxEnabled ? PHConfigs.SANDBOX_URL : PHConfigs.LIVE_URL);
        if (this.cordova != null) {
            this.cordova.startActivityForResult((CordovaPlugin) this, intent, PAYHERE_REQUEST); //unique request ID like private final static int PAYHERE_REQUEST = 11010;        
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        JSONObject responseObj = new JSONObject();

        if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);

            if (response.isSuccess()) {
                responseObj = getFormattedResponse("success",201,"Payment completed successfully",response.getData());
            } else {
                responseObj = getFormattedResponse("error",-1,"Payment failed",response.getData());
            }

        }else{

            if(resultCode == 0 ) {
                responseObj = getFormattedResponse("error",0,"Cancelled by user",null);
            }else{
                responseObj = getFormattedResponse("error",resultCode,"Something went wrong",null);
            }

        }

        Log.d(TAG, responseObj.toString());
        this.callbackContext.success(responseObj.toString());


    }

    private JSONObject getFormattedResponse(String status, int statusCode, String message, StatusResponse data ){
        JSONObject responseObj = new JSONObject();
        try {
            responseObj.put("status", status);
            responseObj.put("statusCode",statusCode);
            responseObj.put("message", message);
            if(data!= null){
            responseObj.put("data",data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return responseObj;
    }
}
