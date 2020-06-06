import payHereSDK
import UIKit

/*
* Notes: The @objc shows that this class & function should be exposed to Cordova.
*/
@objc(Payhere) class Payhere : CDVPlugin,PHViewControllerDelegate {

    var callbackContext:String?
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
        self.commandDelegate!.send(pluginResult, callbackId: self.callbackContext);
    }
    
    func onErrorReceived(error: Error) {
        print("✋ Error",error)
        let pluginResult = CDVPluginResult (status: CDVCommandStatus_ERROR, messageAs: error.localizedDescription);
        self.commandDelegate!.send(pluginResult, callbackId: self.callbackContext);
    }
    
    @objc(checkout:) // Declare checkout function.
    func checkout(command: CDVInvokedUrlCommand) {
        self.callbackContext = command.callbackId
        self.processRequest(command: command, type:"checkout")
    }
    
    @objc(preApprove:) // Declare checkout function.
    func preApprove(command: CDVInvokedUrlCommand) {
        self.callbackContext = command.callbackId
        self.processRequest(command: command, type:"preApprove")
    }
    
    func processRequest(command: CDVInvokedUrlCommand, type:String) {
        let requestData = command.arguments[0] as? [String:AnyObject]
           
        let merchantID = requestData?["merchantId"] as? String ?? ""
        let notifyURL = requestData?["notifyURL"] as? String ?? ""

        // customer details
        var firstName:String = ""
        var lastName:String = ""
        var email:String = ""
        var phone:String = ""
        if let customer = requestData?["customer"] as? [String:AnyObject] {
           firstName = customer["firstName"] as? String ?? ""
           lastName = customer["lastName"] as? String ?? ""
           email = customer["email"] as? String ?? ""
           phone = customer["phone"] as? String ?? ""
        }

        // billing adress details
        var address:String = ""
        var city:String = ""
        var country:String = ""
        if let billing = requestData?["billing"] as? [String:AnyObject] {
           address = billing["address"] as? String ?? ""
           city = billing["city"] as? String ?? ""
           country = billing["country"] as? String ?? ""
        }

        //order details
        let orderID:String? = requestData?["orderId"] as? String
        let itemsDescription:String? = requestData?["description"] as? String
        let currency:PHCurrency =  requestData?["currency"] as? PHCurrency ?? PHCurrency.LKR
        let amount:Double? = requestData?["amount"] as? Double

        //order items
        var itemsMap:[Item] = []
        if let items = requestData?["items"] as? [AnyObject] {
            for item in items {
                let id:String = item["id"] as? String ?? ""
                let name:String = item["name"] as? String ?? ""
                let quantity:Int = item["quantity"] as? Int ?? 1
                let amount:Double = item["id"] as? Double ?? 0.00
                itemsMap.append(Item(id: id, name: name, quantity: quantity, amount: amount))
            }
        }
             
        // delivary adress details
        var deliveryAddress:String = ""
        var deliveryCity:String = ""
        var deliveryCountry:String = ""
        if let billing = requestData?["shipping"] as? [String:AnyObject] {
           deliveryAddress = billing["address"] as? String ?? ""
           deliveryCity = billing["city"] as? String ?? ""
           deliveryCountry = billing["country"] as? String ?? ""
        }

        // optional custom params
        let custom1:String? = requestData?["custom1"] as? String
        let custom2:String? = requestData?["custom2"] as? String
            
        switch type {
        case "checkout":
             self.initRequest = PHInitialRequest(merchantID: merchantID, notifyURL: notifyURL, firstName: firstName, lastName: lastName, email: email, phone: phone, address: address, city: city, country: country, orderID: orderID, itemsDescription: itemsDescription, itemsMap: itemsMap, currency: currency, amount: amount, deliveryAddress: deliveryAddress, deliveryCity: deliveryCity, deliveryCountry: deliveryCountry, custom1: custom1, custom2: custom2)
             break
        case "preApprove":
            self.initRequest = PHInitialRequest(merchantID: merchantID, notifyURL: notifyURL, firstName: firstName, lastName: lastName, email: email, phone: phone, address: address, city: city, country: country, orderID: orderID, itemsDescription: itemsDescription, itemsMap: itemsMap, currency: currency, custom1: custom1, custom2: custom2)
            break
       
        default:
            return
        }
      
        // show payment methods popup
        DispatchQueue.main.async {
        PHPrecentController.precent(from: self.viewController, isSandBoxEnabled: false, withInitRequest: self.initRequest!, delegate: self)
        }
    }
}
