var exec = require("cordova/exec");

exports.checkout = function (arg0, success, error) {
  exec(success, error, "Payhere", "checkout", [arg0]);
};
exports.preApprove = function (arg0, success, error) {
  exec(success, error, "Payhere", "preApprove", [arg0]);
};
