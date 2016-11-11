'use strict';

myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/reservation', {
        templateUrl: 'views/patients/reservation/reservation.html',
        controller: 'ReservationCtrl'
    });
}]);

myApp.controller('ReservationCtrl', ['REST_API', '$rootScope', '$scope', '$http', '$location', 'UserService',
    function (REST_API, $rootScope, $scope, $http, $location, UserService) {

    $rootScope.userDetails = UserService.getUser();
    
    for (var callBtn of $(".call-btn")) {
    	window.setInterval(function checkIfAvailable() {
    		$http.get()
    	}, 5000);
    }
    
    $http.get(REST_API + "patients/" + $rootScope.userDetails.id + "/current-reservations")
	    .then(function successCallback(response) {
			$scope.reservations	 = response.data;
			
		}, function errorCallback(response) {
			console.log("[ERROR]: " + response.data.message);
		});
}]);