'use strict';


myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/logout', {
        templateUrl: 'views/logout/logout.html',
        controller: 'LogoutCtrl'
    });
}])

myApp.controller('LogoutCtrl', ['REST_API', '$rootScope', '$scope', '$http', '$location', 'UserService',
    function (REST_API, $rootScope, $scope, $http, $location, UserService) {
        if ($rootScope.user !== 'undefined') {

            var request = {
                method: "GET",
                url: REST_API + "logout",
            };

            $http(request).then(function successCallback() {
                UserService.setUser({})
                delete $rootScope.userDetails;
                console.log('User has logged out');

            }, function errorCallback(response) {
                console.error('Logout failure');
            });
        }

        $location.url('/');
    }]);