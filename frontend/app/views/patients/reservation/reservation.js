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
		
		function checkIfPatientAllowed(doctorAvailable, reservation) {
			var now = new Date();
			var timezoneOffset = now.getTimezoneOffset();
			var startTimeWithOffset = new Date(reservation.startDateTime).getTime() 
				+ (timezoneOffset * 60 * 1000);
			
			var isAllowed = doctorAvailable
				&& (startTimeWithOffset <= now.getTime());

			var callBtn = $("#call-btn-" + reservation.id);
			callBtn.attr("disabled", !isAllowed);
			
			if (isAllowed) {
				callBtn.addClass("btn-success");
				callBtn.removeClass("btn-danger");
			} else {
				callBtn.addClass("btn-danger");
				callBtn.removeClass("btn-success");
			}
			
			return isAllowed;
		}
		
		$scope.getCurrentReservation = function () {
	        $http.get(REST_API + "patients/" + $rootScope.userDetails.id + "/reservations")
	            .then(function successCallback(response) {
	                $scope.reservations	 = response.data;
	                
	                console.log($scope.reservations);
	                
	                var now = new Date();
	                var millisPerDay = 24 * 60 * 60 * 1000;
	                
	                /* We need to wait a while before the call buttons are 
	                 * loaded in a ng-repeat element.
	                 * */
	                
	                setTimeout(function () {
	                	$scope.stompClients = []
	                	
	                	for (var reservation of response.data) {
	                		var startDate = new Date(reservation.startDateTime);
	                		var timeDelta = Math.abs(now.getTime() - startDate.getTime()) / millisPerDay;
	                		
	                		if (timeDelta <= 1) {
	                			var socket = new SockJS(REST_API + "ws")
	                			var stompClient = Stomp.over(socket);
	                			$scope.stompClients.push(stompClient);
	                			
	                			checkIfPatientAllowed(reservation.doctorAvailable, reservation);
	                			
	                			stompClient.connect({}, function (frame) {
	                				stompClient.subscribe("/topic/doctors/" + reservation.doctorId + "/available", function (availabilityStatus) {
	                					var patientAllowed = checkIfPatientAllowed(
	                							(availabilityStatus.body === "true"), reservation);
	                					
	                					console.log("Doctor [" + reservation.doctorName 
	                							+ "]'s availability status has changed to: " 
	                							+ availabilityStatus.body);
	                				});
	                			}, function () {
	                				console.log("[ERROR]: Websocket connection error");
	                			});
	                		}
	                	}
	                }, 1000);
	                
	            }, function errorCallback(response) {
	            	console.log("[ERROR]: " + response.data.message);
	            });
	    }
		
	    $scope.call = function (reservation) {
	    	for (var client of $scope.stompClients) {
	    		client.disconnect(function () {
	    			console.log("Socket closed");
	    		});
	    	}
	    	
	    	$rootScope.reservation = reservation;
	    	$location.url("/patient-consultation");
	    }
	    
	    $scope.getCurrentReservation();
	}]);