'use strict';

myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/passwords/:refreshToken', {
        templateUrl: 'views/setNewPassword/setNewPassword.html',
        controller: 'PasswordRefreshingCtrl'
    });
}]);

myApp.controller('PasswordRefreshingCtrl', ['REST_API', "$rootScope", '$scope', '$http', '$location', 'UserService', '$routeParams',
    function (REST_API, $rootScope, $scope, $http, $location, UserService, $routeParams) {
        var refreshToken = $routeParams.refreshToken;

        $scope.setPassword = function () {
            var password = {
                password1: $scope.user.password1,
                password2: $scope.user.password2
            };

            $http.post(REST_API + "passwords/" + refreshToken, password)
                .then(function successCallback(response) {
                    console.log("Success");
                    $location.url("/login");
                }, function errorCallback(response) {
                    console.log("[ERROR]: " + response.data.message);
                })
        }
    }]);