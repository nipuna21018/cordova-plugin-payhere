package lk.nsoft.cordova.plugins;

import org.apache.cordova.CordovaPlugin;

import com.google.gson.JsonObject;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
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
            this.checkout(message, callbackContext);
            return true;
        }
        if (action.equals("preApprove")) {
            String message = args.getString(0);
            this.preApprove(message, callbackContext);
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
        try {
            JSONObject data = new JSONObject(message);
            req.setMerchantId(data.getString("merchantId")); // Your Merchant ID
            req.setMerchantSecret(data.getString("merchantSecret")); // Your Merchant secret
            req.setAmount(data.getDouble("amount")); // Amount which the customer should pay
            req.setCurrency(data.getString("currency")); // Currency
            req.setOrderId(data.getString("orderId")); // Unique ID for your payment transaction
            req.setItemsDescription(data.getString("itemsDescription")); // Item title or Order/Invoice number
            req.setCustom1(data.getString("custom1"));
            req.setCustom2(data.getString("custom2"));
            req.getCustomer().setFirstName(data.getJSONObject("customer").getString("firstName"));
            req.getCustomer().setLastName(data.getJSONObject("customer").getString("lastName"));
            req.getCustomer().setEmail(data.getJSONObject("customer").getString("email"));
            req.getCustomer().setPhone(data.getJSONObject("customer").getString("phone"));
            req.getCustomer().getAddress().setAddress(data.getJSONObject("billing").getString("address"));
            req.getCustomer().getAddress().setCity(data.getJSONObject("billing").getString("city"));
            req.getCustomer().getAddress().setCountry(data.getJSONObject("billing").getString("country"));
            req.getCustomer().getDeliveryAddress().setAddress(data.getJSONObject("shipping").getString("address"));
            req.getCustomer().getDeliveryAddress().setCity(data.getJSONObject("shipping").getString("city"));
            req.getCustomer().getDeliveryAddress().setCountry(data.getJSONObject("shipping").getString("country"));
            //req.getItems().add(new Item(null, "Door bell wireless", 1));
        } catch (JSONException $je) {
            callbackContext.error("Invalid data!");
        }
        Intent intent = new Intent(cordova.getActivity(), PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
        if (this.cordova != null) {
            this.cordova.startActivityForResult((CordovaPlugin) this, intent, PAYHERE_REQUEST); //unique request ID like private final static int PAYHERE_REQUEST = 11010;        
        }
    }
    private void preApprove(String message, CallbackContext callbackContext) {
        LOG.d(TAG, message);
        this.callbackContext = callbackContext;
        InitPreapprovalRequest req = new InitPreapprovalRequest();
        try {
            JSONObject data = new JSONObject(message);
            req.setMerchantId(data.getString("merchantId")); // Your Merchant ID
            req.setMerchantSecret(data.getString("merchantSecret")); // Your Merchant secret
            req.setOrderId("230000125");        // Unique Reference ID
            req.setCurrency("LKR");             // Currency code of future payments
            req.setItemsDescription("1 Greeting Card");
            req.setCustom1("This is the custom 1 message");
            req.setCustom2("This is the custom 2 message");
            req.getCustomer().setFirstName("Saman");
            req.getCustomer().setLastName("Perera");
            req.getCustomer().setEmail("samanp@email.com");
            req.getCustomer().setPhone("+947771234567");
            req.getCustomer().getAddress().setAddress("No.01, Galle Road,");
            req.getCustomer().getAddress().setCity("Colombo");
            req.getCustomer().getAddress().setCountry("Sri Lanka");
            //req.getItems().add(new Item(null, "Door bell wireless", 1));
        } catch (JSONException $je) {
            callbackContext.error("Invalid data!");
        }
        Intent intent = new Intent(cordova.getActivity(), PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
        if (this.cordova != null) {
            this.cordova.startActivityForResult((CordovaPlugin) this, intent, PAYHERE_REQUEST); //unique request ID like private final static int PAYHERE_REQUEST = 11010;        
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO process response
        if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data
                    .getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
            String msg;
            if (response.isSuccess()) {
                msg = "Activity result:" + response.getData().toString();
                this.callbackContext.success(msg);
            } else {
                msg = "Result:" + response.toString();
                this.callbackContext.success(msg);
            }
            LOG.d(TAG, msg);
            //textView.setText(msg);
        }
    }

}
