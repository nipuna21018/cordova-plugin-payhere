import payHereSDK
import UIKit

/*
* Notes: The @objc shows that this class & function should be exposed to Cordova.
*/
@objc(Payhere) class Payhere : CDVPlugin,PHViewControllerDelegate {

    var mCalendarAccountsCallbackContext:String?
    let merchandID = "<<>>"
    var initRequest : PHInitialRequest?
    
    func onResponseReceived(response: PHResponse<Any>?) {
        let pluginResult:CDVPluginResult;
        if(response?.isSuccess())!{
            guard let resp = response?.getData() as? StatusResponse else{ return }
                   
            //Payment Sucess
            pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: resp.toJSONString());
                   
        }else{
            let msg = response?.getMessage()
            pluginResult = CDVPluginResult (status: CDVCommandStatus_ERROR, messageAs: msg);   
        }
        
        // Send the function result back to Cordova.
        self.commandDelegate!.send(pluginResult, callbackId: self.mCalendarAccountsCallbackContext);
    }
    
    func onErrorReceived(error: Error) {
        print("✋ Error",error)
        let pluginResult = CDVPluginResult (status: CDVCommandStatus_ERROR, messageAs: error.localizedDescription);
        self.commandDelegate!.send(pluginResult, callbackId: self.mCalendarAccountsCallbackContext);
    }
    
  @objc(checkout:) // Declare your function name.
  func checkout(command: CDVInvokedUrlCommand) { // write the function code.
    self.mCalendarAccountsCallbackContext = command.callbackId
    
    let item1 = Item(id: "001", name: "PayHere Test Item 01", quantity: 1, amount: 25.00)
    let item2 = Item(id: "002", name: "PayHere Test Item 02", quantity: 2, amount: 25.0)

    self.initRequest = PHInitialRequest(merchantID: self.merchandID, notifyURL: "", firstName: "Pay", lastName: "Here", email: "test@test.com", phone: "+9477123456", address: "Colombo", city: "Colombo", country: "Sri Lanka", orderID: "001", itemsDescription: "PayHere SDK Sample", itemsMap: [item1,item2], currency: .LKR, amount: 50.00, deliveryAddress: "", deliveryCity: "", deliveryCountry: "", custom1: "custom 01", custom2: "custom 02")
    
    PHPrecentController.precent(from: self.viewController, isSandBoxEnabled: false, withInitRequest: self.initRequest!, delegate: self)

  }
}
