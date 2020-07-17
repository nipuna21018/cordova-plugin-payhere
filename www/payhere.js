var exec = require("cordova/exec");
var PLUGIN_NAME = "Payhere";

var Payhere = function () {};
Payhere.checkout = function (requestData, successCallback, failureCallback) {
  cordova.exec(successCallback, failureCallback, "Payhere", "checkout", [
    requestData,
  ]);
};
Payhere.preApprove = function (requestData, successCallback, failureCallback) {
  cordova.exec(successCallback, failureCallback, "Payhere", "preApprove", [
    requestData,
  ]);
};
module.exports = Payhere;
