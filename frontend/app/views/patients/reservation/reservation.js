'use strict';

myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/reservation', {
        templateUrl: 'views/patients/reservation/reservation.html',
        controller: 'ReservationCtrl'
    });
}]);

myApp.controller('ReservationCtrl', ['REST_API', '$rootScope', '$scope', '$http', '$location', 'UserService',
    function (REST_API, $rootScope, $scope, $http, $location, UserService) {
		$rootScope.userDetails = UserService.getUserOrRedirect($location, "/login");
	
		/* Warning: 
		 * The date info from the server doesn't contain the 
		 * timezone data. Because of that we need to add the 
		 * offset specific to the client's timezone before
		 * comparing the two dates.
		 **/
		
		$scope.reservationStarted = function (reservation) {
			var now = new Date();
			var timezoneOffset = now.getTimezoneOffset();
			var startTimeWithOffset = new Date(reservation.startDateTime).getTime() 
				+ (timezoneOffset * 60 * 1000);
			
			return (startTimeWithOffset <= now.getTime());
		}
		
		$scope.getCurrentReservation = function () {
	        $http.get(REST_API + "patients/" + $rootScope.userDetails.id + "/reservations")
	            .then(function successCallback(response) {
	                $scope.reservations	 = response.data;
	                
	            }, function errorCallback(response) {
	            	console.log("[ERROR]: Couldn't load reservation data");
	            });
	    }
		
	    $scope.call = function (reservation) {
	    	$rootScope.reservation = reservation;
	    	$location.url("/patient-consultation");
	    }
	    
	    $scope.getCurrentReservation();
	}]);