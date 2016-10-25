'use strict';

angular.module('myApp.signUp', ['ngRoute'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/signUp', {
            templateUrl: 'views/signUp/signUp.html',
            controller: 'SignUpCtrl'
        });
    }])

    .controller('SignUpCtrl', ['REST_API', '$scope', '$http', '$location', function (REST_API, $scope, $http, $location) {
        console.log("sign up " + REST_API, $scope);

        $scope.create = function () {
            console.log("POST " + REST_API + "signup/save", $scope.user)

            $http(
                {
                    method: "POST",
                    url: REST_API + "signup/save",
                    data: $scope.user
                }
            ).then(function successCallback() {
                $location.url('/' + "login");
            }, function errorCallback(response) {
                console.error(response.data.message)
            });
        };

    }]);