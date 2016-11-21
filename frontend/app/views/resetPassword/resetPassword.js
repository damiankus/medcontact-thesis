'use strict';


myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/reset-password', {
        templateUrl: 'views/resetPassword/resetPassword.html',
        controller: 'ResetPasswordCtrl'
    });
}]);

myApp.controller('ResetPasswordCtrl', ['REST_API', "$rootScope", '$scope', '$http', '$location', 'UserService',
    function (REST_API, $rootScope, $scope, $http, $location, UserService) {

        $scope.resetPassword = function () {
            var request = {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                url: REST_API + "reset-password/send-link",
                transformRequest: function (obj) {
                    var str = [];
                    for (var p in obj)
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    return str.join("&");
                },
                data: {
                    email: $scope.userDetails.email
                }
            };

            $http(request)
                .then(function successCallback(response) {
                    console.log("Success")
                }, function errorCallback(response) {
                    console.log("[ERROR]: " + response.data.message);
                })
        }
    }]);