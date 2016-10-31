'use strict';

angular.module('myApp.logout', ['ngRoute'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/logout', {
            templateUrl: 'views/logout/logout.html',
            controller: 'LogoutCtrl'
        });
    }])

    .controller('LogoutCtrl', ['REST_API', '$rootScope', '$scope', '$http', '$location',
        function (REST_API, $rootScope, $scope, $http, $location) {
    		if ($rootScope.user !== 'undefined') {
    			
    			 var request = {
					method: "GET",
					url: REST_API + "logout",
				};
    			
    			$http(request).then(function successCallback() {
    				$rootScope.role = '';
    				console.log('User has logged out');
    				
    			}, function errorCallback(response) {
    				console.error('Logout failure');
    			});
    		}
    		
    		$rootScope.user = {};
    		$location.url('/');
    		console.log("cUR ID: " + $rootScope.user.id);
        }]);