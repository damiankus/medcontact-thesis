'use strict';


myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/passwords/:refreshToken/set', {
        templateUrl: 'views/setNewPassword/setNewPassword.html',
        controller: 'PasswordRefreshingCtrl'
    });
}]);

myApp.controller('PasswordRefreshingCtrl', ['REST_API', "$rootScope", '$scope', '$http', '$location', 'UserService',
    function (REST_API, $rootScope, $scope, $http, $location, UserService) {

        var url = $location.url().split("/");
        var refreshToken = url[url.size()-1]

        $scope.setPassword = function () {
            var password = {
                password1: $scope.user.password1,
                password2: $scope.user.password2
            }

            $http.post(REST_API + "passwords/" + refreshToken + "/set", password)
                .then(function successCallback(response) {
                    console.log("Success");
                    $location.url("/login");
                }, function errorCallback(response) {
                    console.log("[ERROR]: " + response.data.message);
                })
        }
    }]);