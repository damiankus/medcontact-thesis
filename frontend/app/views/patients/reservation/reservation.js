'use strict';

myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/reservation', {
        templateUrl: 'views/patients/reservation/reservation.html',
        controller: 'ReservationCtrl'
    });
}]);

myApp.controller('ReservationCtrl', ['REST_API', '$rootScope', '$scope', '$http', '$location', 'UserService', 'TimeService',
    function (REST_API, $rootScope, $scope, $http, $location, UserService, TimeService) {
		$rootScope.userDetails = UserService.getUserOrRedirect($location, "/login");
	
		/* Warning: 
		 * The date info from the server doesn't contain the 
		 * timezone data. Because of that we need to add the 
		 * offset specific to the client's timezone before
		 * comparing the two dates.
		 **/
		
		$scope.sortField = "startDateTime";
		$scope.sortReversed = false;
		
		$scope.reservationStarted = function (reservation) {
			return (reservation.startDateTime.getTime() <= new Date().getTime());
		}
		
		$scope.reservationFinished = function (reservation) {
			return (new Date().getTime() > reservation.endDateTime.getTime());
		}
		
		$scope.getCurrentReservations = function () {
	        $http.get(REST_API + "patients/" + $rootScope.userDetails.id + "/reservations")
	            .then(function successCallback(response) {
	                $scope.reservations	 = response.data;
	                
	                $scope.reservations.forEach(function (reservation, index) {
	                	reservation.startDateTime = new Date(TimeService.parseWithTimezone(reservation.startDateTime));
	                	reservation.endDateTime = new Date(TimeService.parseWithTimezone(reservation.endDateTime));
	                });
	                
	            }, function errorCallback(response) {
	            	console.log("[ERROR]: Couldn't load reservation data");
	            });
	    }

		
		$scope.showCancelReservationModal = function (reservationId) {
        	$scope.cancelledReservationId = reservationId;
        	
        	var dialog = $("#modal-cancel-reservation");
			dialog.modal("show");
        }
		
		$scope.hideCancelReservationModal = function () {
        	$scope.cancelledReservationId = -1;
        	
        	var dialog = $("#modal-cancel-reservation");
			dialog.modal("hide");
        }
		
		$scope.cancelReservation = function () {
			$http.delete(REST_API + "patients/" + $rootScope.userDetails.id + "/reservations/" + $scope.cancelledReservationId)
            .then(function successCallback(response) {
            	$scope.getCurrentReservations();
            	console.log("[INFO]: Reservation has been cancelled")
                
            }, function errorCallback(response) {
            	console.log("[ERROR]: Couldn't cancel reservation");
            });
			
			$scope.hideCancelReservationModal();
		};
		
	    $scope.call = function (reservation) {
	    	$rootScope.reservation = reservation;
	    	$location.url("/patient/consultation");
	    }
	    
	    $scope.getCurrentReservations();
	}]);