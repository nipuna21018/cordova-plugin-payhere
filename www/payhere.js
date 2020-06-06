var exec = require("cordova/exec");
var PLUGIN_NAME = "Payhere";

var Payhere = function () {};
Payhere.checkout = function (requestData) {
  return new Promise((resolve, reject) => {
    cordova.exec(resolve, reject, "Payhere", "checkout", [requestData]);
  });
};
Payhere.preApprove = function (requestData) {
  return new Promise((resolve, reject) => {
    cordova.exec(resolve, reject, "Payhere", "preApprove", [requestData]);
  });
};
module.exports = Payhere;
