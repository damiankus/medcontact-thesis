'use strict';

myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/add-schedule', {
        templateUrl: 'views/doctors/add-schedule/add-schedule.html',
        controller: 'AddScheduleCtrl'
    });
}]);

var isValidDate = function (date) {
    if (Object.prototype.toString.call(date) === "[object Date]") {
    	
        // it is a date
        if (isNaN(date.getTime())) {  // d.valueOf() could also work
            console.error("date is not valid");
            return false;
        }
        else {
            // date is valid
            return true;
        }
    }
    else {
        console.error("[ERROR]: It is not a date");
        return false;
    }
};

myApp.controller('AddScheduleCtrl', ['REST_API', "$rootScope", '$scope', '$http', '$location', 'UserService',
    function (REST_API, $rootScope, $scope, $http, $location, UserService) {
        $rootScope.userDetails = UserService.getUserOrRedirect($location, "/login");
        subscribeForNotifications();
        getSchedule();
        $scope.emptySchedule = false;
        
        function subscribeForNotifications() {
	    	var socket = new SockJS(REST_API + "ws")
			var stompClient = Stomp.over(socket);
	    	$rootScope.stompClient = stompClient;
	    	stompClient.debug = null;
	    	$rootScope.ringTone = new Audio("assets/sounds/ring.mp3");
	    	
	    	stompClient.connect({}, function (frame) {
	    		$rootScope.subscription = stompClient.subscribe("/queue/doctors/" + $rootScope.userDetails.id + "/calling", function (message) {
	    			$rootScope.prevPatient = $rootScope.callingPatient;
					$rootScope.callingPatient = JSON.parse(message.body);
					$rootScope.ringTone.play();
					$rootScope.patientCalling = true;

					$rootScope.currentReservation = $rootScope.callingPatient.reservation;
					
					var dialog = $("#modal-calling");
					$("#calling-patient-id").text($scope.callingPatient.id);
					$("#calling-patient-name").text($scope.callingPatient.name);
					
					var startTime = formatTime(new Date($scope.callingPatient.reservation.startDateTime));
					
					$("#calling-patient-start").text(startTime);
					$("#redirect-to-consultation-btn").one("click", function () {
						dialog.modal("hide");
						$rootScope.ringTone.pause();
						$rootScope.ringTone.currentTime = 0;
						
						if ($location.url() !== "/doctor/consultation") {
							$location.path("/doctor/consultation");
							
						} else {
							getSharedFiles();
							getNextReservation($rootScope.currentReservation.id);
							getNotesForPatient($rootScope.callingPatient.id);
						}
					});
					
					dialog.modal("show");
	    		});
	    	}, 
	    	{
				id: $rootScope.userDetails.id
			});
	    }

        function getSchedule() {
            $http.get(REST_API + "doctors/" + $rootScope.userDetails.id + "/reservations")
                .then(function successCallback(response) {
                        response.data.forEach(function (schedule) {
                            schedule.startDateTime = new Date(schedule.startDateTime);
                            schedule.endDateTime = new Date(schedule.endDateTime);
                            schedule.day = moment(schedule.startDateTime).format("DD MM YYYY");
                        });
                        response.data = _.groupBy(response.data, function (schedule) {
                            return schedule.day;
                        });

                        $scope.schedules = [];
                        for (var key in response.data) {
                            if (response.data.hasOwnProperty(key)) {
                                $scope.schedules.push({key:new Date(moment(key, "DD MM YYYY")), values:response.data[key]})
                            }
                        }

                        if (!(typeof $scope.schedules !== 'undefined' && $scope.schedules.length > 0)) {
                            $scope.emptySchedule = true;
                        }
                    },
                    function errorCallback(response) {
                        console.log("[ERROR]: " + response.data.message);
                    }
                )
        }


        $scope.addSchedule = function () {
            moment.locale('pl');
            var start = new Date(moment($scope.date + " " + $scope.startTime, "D MMMM YYYY H:mm"));
            var end = new Date(moment($scope.date + " " + $scope.endTime, "D MMMM YYYY H:mm"));

            if (isValidDate(start) && isValidDate(end)) {
                $http.post(REST_API + "doctors/" + $rootScope.userDetails.id + "/reservation", {
                    start: start,
                    end: end
                })
                    .then(function successCallback(response) {
                        getSchedule();
                        $scope.emptySchedule = false;
                        console.log("Success");
                    }, function errorCallback(response) {
                        console.log("[ERROR]: " + response.data.message);
                    })
            }
            else {
                console.error("Wrong data", $scope.date, $scope.startTime, $scope.endTime)
            }

        };
        
        $(function () {
            $('#datePickerDiv').datetimepicker({
                locale: 'pl',
                format: 'D MMMM YYYY',
                minDate:Date.now(),
            }).on("dp.change", function () {
                $scope.date = $("#datePicker").val();
            });

            $('#startTimePickerDiv').datetimepicker({
                locale: 'pl',
                format: 'H:mm',
                maxDate:(new Date()).setHours(23, 59, 59, 0)
            }).on("dp.change", function (e) {
                $scope.startTime = $("#startTimePicker").val();
                $('#endTimePickerDiv').data("DateTimePicker").minDate(e.date)
            });

            $('#endTimePickerDiv').datetimepicker({
                locale: 'pl',
                format: 'H:mm',
                minDate:Date.now(),
                maxDate:(new Date()).setHours(23, 59, 59, 0)
            }).on("dp.change", function (e) {
                $scope.endTime = $("#endTimePicker").val();
                $('#startTimePickerDiv').data("DateTimePicker").maxDate(e.date);
            });
        });
        
        function formatTime(dateTime) {
        	var time = new Date(dateTime);
    		return  "" + ((time.getHours() > 9) ? time.getHours() : "0" + time.getHours()) 
    			+ ":" + ((time.getMinutes() > 9) ? time.getMinutes() : "0" + time.getMinutes());
        }
    }]);