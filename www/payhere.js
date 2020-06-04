var exec = require('cordova/exec');

exports.checkout = function (arg0, success, error) {
    exec(success, error, 'Payhere', 'checkout', [arg0]);
};