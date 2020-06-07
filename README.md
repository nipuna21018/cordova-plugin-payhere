# Ionic Cordova Native Plugin for [PayHere](https://payhere.lk) Payment Gateway

This plugin can be used to add native behaviour of PayHere mobile SDK into your Ionic cordova project. Plugin supports two platforms Android & IOS

# Installation

---

### Plugin

Add the plugin and type definitions to your project

```
    cordova plugin add cordova-plugin-payhere
    npm install types-payhere --save
```

It is also possible to install via repo url directly ( unstable )

```
    cordova plugin add https://github.com/nipuna21018/cordova-plugin-payhere.git
```

### CocoaPods

Note: This is only if you need plugin to support ios platform

CocoaPods is a dependency manager used in ios projects. We need to add PayHere's native ios SDK dependancy to our project.

You can install CocoaPods with the following command, ignore if CocoaPods already installed on your computer

```bash
$ gem install cocoapods
```

Follow these teps to `payhereSDK` into your ios project

- Go to platform/ios folder in your ionic project
- Open `Podfile` inside `ios` folder. If `Podfile` not available. Simply run the command `pod init` on that folder
- Now you need to add two line to the pod file `use_frameworks!` & `pod 'payHereSDK' , '2.0.0'`
  -- example `podfile`

```ruby
platform :ios, '11.0'
use_frameworks!
target '<your project name>' do
	project '<your project name>.xcodeproj'
	pod 'payHereSDK', '2.0.0'
end
```

- Now run following command to install all dependancies

```bash
$ pod install
```

- That's all. Now all dependencies are installed on your ios project

# Usage

---

Add the plugin to app's provider list

```ts
import { PayHere } from "types-payhere/ngx";

@NgModule({
  imports:  [ ... ],
  declarations: [ ... ],
  providers: [..., PayHere],
})
export class AppModule {}
```

In your page

### Checkout Payment Request:

```ts
import { PayHere } from "types-payhere/ngx";

constructor(private payhere: PayHere) { }

...

const checkoutRequest: CheckoutRequest = {
        sandboxEnabled:true // default is false
        merchantId: "11111",
        amount: 10.5,
        currency: "LKR",
        orderId: "123",
        itemsDescription: "Desc",
        custom1: "",
        custom2: "",
        customer: {
          firstName: "Nipuna",
          lastName: "Harshana",
          email: "nipuna@comapny.com",
          phone: "0712345678",
        },
        billing: {
          address: "No 127, Street name",
          city: "Colombo",
          country: "Sri Lanka",
        },
        shipping: {
          address: "Park Street",
          city: "Colombo",
          country: "Sri Lanka",
        },
        items: [
          {
            id: "1",
            name: "My first item name",
            quantity: 1,
            amount: 10.0,
          },
          {
            id: "2",
            name: "My second item name",
            quantity: 1,
            amount: 20.0,
          },
        ],
      }

this.payhere.checkout(checkoutRequest).then((response) => {
 // handle response
}, (err) => {
 // Handle error
});
```

### PreAproval Payment Request:

```ts
import { PayHere } from "types-payhere/ngx";

constructor(private payhere: PayHere) { }

...

const preApproveRequest: PreApproveRequest = {
        sandboxEnabled:true // default is false
        merchantId: "11111",
        currency: "LKR",
        orderId: "123",
        itemsDescription: "Desc",
        custom1: "",
        custom2: "",
        customer: {
          firstName: "Nipuna",
          lastName: "Harshana",
          email: "nipuna@comapny.com",
          phone: "0712345678",
        },
        billing: {
          address: "No 127, Street name",
          city: "Colombo",
          country: "Sri Lanka",
        },
        items: [],
      }

this.payhere.preApprove(preApproveRequest).then((response) => {
 // handle response
}, (err) => {
 // Handle error
});
```

### Recurring Payment Request:

```
not supported yet
```

# PayHere API & SDK reference

---

See [Android SDK](https://support.payhere.lk/api-&-mobile-sdk/mobile-sdk-for-android), [iOS SDK](https://support.payhere.lk/api-&-mobile-sdk/mobile-sdk-for-ios)

# Todos

---

- Add support for Recurring Payment Request
- Add browser support

# License

---

MIT
