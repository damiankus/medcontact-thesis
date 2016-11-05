'use strict';

myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/signUp', {
        templateUrl: 'views/signUp/signUp.html',
        controller: 'SignUpCtrl'
    });
}])

myApp.controller('SignUpCtrl', ['REST_API', '$scope', '$rootScope', '$http', '$location',
    function (REST_API, $scope, $rootScope, $http, $location) {

    $scope.signupError = false;

    $scope.create = function () {
        console.log("POST " + REST_API + "signup/save", $scope.user);

        $http(
            {
                method: "POST",
                url: REST_API + "signup/save",
                data: $scope.user
            }
        ).then(function successCallback() {

            /* If a user has managed to successfully create an account
             * he/she should be redirected to the reservations view */

            var request = {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                url: REST_API + "login",
                transformRequest: function (obj) {
                    var str = [];
                    for (var p in obj)
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    return str.join("&");
                },
                data: {
                    username: $scope.user.email,
                    password: $scope.user.password
                }
            };

            $http(request).then(function successCallback(response) {
                $rootScope.userDetails = response.data;
                $scope.loginError = false;
                $scope.loggedIn = true;
                $location.url('/reservation');

            }, function errorCallback(response) {
                console.error(response.data.message);
                $scope.loginError = true;
                $scope.loggedIn = false;
                $location.url('/' + "login");
            });
            $scope.signupError = false;
        }, function errorCallback(response) {
            console.error(response.data.message);
            $scope.signupError = true;
        });
    };

}]);