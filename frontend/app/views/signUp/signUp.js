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
            $http.post(REST_API + "signup/save", $scope.user)
                .then(function successCallback() {
                    $scope.loginError = false;
                    $scope.loggedIn = false;
                    $location.url('/login');
                }, function errorCallback(response) {
                    console.error(response.data.message);
                    $scope.signupError = true;
                });
        };

    }]);