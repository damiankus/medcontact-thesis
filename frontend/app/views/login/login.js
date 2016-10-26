'use strict';

angular.module('myApp.login', ['ngRoute'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/login', {
            templateUrl: 'views/login/login.html',
            controller: 'LoginCtrl'
        });
    }])

    .controller('LoginCtrl', ['REST_API', 'ROLE', '$rootScope', '$scope', '$http', '$location',
        function (REST_API, ROLE, $rootScope, $scope, $http, $location) {
            console.log("LOGGER: LoginCtr, api: " + REST_API);

            $scope.login = function () {
                var request = {
                    method: "POST",
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    url: REST_API + "login",
                    transformRequest: function (obj) {
                        var str = [];
                        for (var p in obj)
                            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                        return str.join("&");
                    },
                    data: $scope.user
                };

                $http(request).then(function () {
                    $rootScope.role = "PATIENT";
                    console.log("SUCCESS ");
                    $location.url('/' + "reservation");
                }, function errorCallback(response) {
                    console.error(response.data.message)
                });

            }

        }]);