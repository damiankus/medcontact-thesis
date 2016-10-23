'use strict';

angular.module('myApp.signUp', ['ngRoute'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/signUp', {
            templateUrl: 'views/signUp/signUp.html',
            controller: 'SignUpCtrl'
        });
    }])

    .controller('SignUpCtrl', ['REST_API', '$scope', '$http', function (REST_API, $scope, $http) {
        console.log("sign up " + REST_API, $scope);

        $scope.create = function () {
            console.log("POST " + REST_API + "signup/save", $scope.user)

            $http(
                {
                    method: "POST",
                    url: REST_API + "signup/save",
                    data: {"firstName": $scope.user.firstName},
                }
            ).then(onSuccess(), onFailure());

        };

        function onSuccess(response) {
            console.log("SUCCESS ", response)
        }

        function onFailure(response) {
            console.log("FAILURE", response)
        }
    }]);