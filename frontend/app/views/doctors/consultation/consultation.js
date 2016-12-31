'use strict';

myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/doctor/consultation', {
        templateUrl: 'views/doctors/consultation/consultation.html',
        controller: 'ConsultationDoctorCtrl'
    });
}]);

myApp.controller('ConsultationDoctorCtrl', ['REST_API', "$rootScope", '$scope', '$http', '$location', 'UserService', 'TimeService',
  function (REST_API, $rootScope, $scope, $http, $location, UserService, TimeService) {
	$rootScope.userDetails = UserService.getUserOrRedirect($location, "/add-schedule");
	$scope.isNoteModified = {};
	initControlls();
	
	if (typeof $rootScope.callingPatient !== "undefined") {
		getSharedFiles();
		getNextReservation($rootScope.currentReservation.id);
		getNotesForPatient($rootScope.callingPatient.id);
	}
	
	$http.get(REST_API + "doctors/" + $rootScope.userDetails.id + "/connection")
	    .then(function successCallback(response) {
	    	$scope.connectionDetails = response.data;
	    	$rootScope.ringTone = new Audio("assets/sounds/ring.mp3");
	    	
	    	if ($scope.connectionDetails === undefined
	        		|| $scope.connectionDetails === null) {
	            console.error("[ERROR]: Invalid connection data");
	            $location.url("/login");

	        } else {
	            getCredentials();
	        }
	    	
	    }, function errorCallback(response) {
	    	alert("[ERROR]: Couldn't load connection data");
	    	$location.url("/login");
	    });
	
    $scope.webrtc = {};
    var remotes = {
       	 volume: 0.5
    };
    
    /*
     * Rendering shared files
     * */
    
	$scope.sortField = "name";
	$scope.sortReversed = true;
	
	if (!$rootScope.subscribed) {
		subscribeForNotifications();
	}

	/* 
	 * Connection logic
	 * */
	
	 function subscribeForNotifications() {
	    	var socket = new SockJS(REST_API + "ws")
			var stompClient = Stomp.over(socket);
	    	$rootScope.stompClient = stompClient;
	    	stompClient.debug = null;
	    	
	    	stompClient.connect({}, function (frame) {
	    		$rootScope.subscribed = true;
	    		$rootScope.subscription = stompClient.subscribe("/queue/doctors/" + $rootScope.userDetails.id + "/calling", function (message) {
	    			$rootScope.prevPatient = $rootScope.callingPatient;
					$rootScope.callingPatient = JSON.parse(message.body);
					$rootScope.ringTone.play();
					$rootScope.patientCalling = true;

					$rootScope.currentReservation = $rootScope.callingPatient.reservation;
					
					var dialog = $("#modal-calling");
					$("#calling-patient-id").text($scope.callingPatient.id);
					$("#calling-patient-name").text($scope.callingPatient.name);
					
					$("#calling-patient-start").text(TimeService.parseTimeWithTimezone($scope.callingPatient.reservation.startDateTime));
					var startTime = TimeService.parseTimeWithTimezone($scope.callingPatient.reservation.startDateTime);
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
	
    function getCredentials() {
        var peerConnectionConfig;
      
        $.ajax({
            url: $scope.connectionDetails.iceEndpoint,
            data: $scope.connectionDetails,
            success: function (data, status) {
                peerConnectionConfig = data.d;

                /* data.d is where the iceServers object lives */

                if (!peerConnectionConfig) {
                    console.error("[ERROR]: Connection attempt has failed");
                    
                } else {
                	initListeners(peerConnectionConfig);
                }
            },
            error: function () {
            	console.error("[ERROR]: TURN server error");
            }
        });
    }
    
    $scope.connect = function () {
		console.log("Connecting to [" + $scope.userDetails.name + "]'s room");
		$scope.connected = true;
		startTransmission();
	}
    
    $scope.disconnect = function () {
    	$scope.webrtc.leaveRoom();
    	stopTransmission();
    	$rootScope.patientCalling = false;
    	$scope.connected = false;
		console.log("$scope.disconnected from: [" + $scope.connectionDetails.room + "]");
    }
    
    function sendStatus(patientId, status) {
    	$scope.stompClient.send("/queue/patients/" + patientId + "/notifications",
			{},
			JSON.stringify({
				status: status
			}));
    }
    
    function terminatePrevConsultation() {
    	var patientId;
    	
    	if (typeof $rootScope.prevPatient !== "undefined") {
    		patientId = $rootScope.prevPatient.id;
    		
    	} else if (typeof $rootScope.callingPatient !== "undefined") {
    		patientId = $rootScope.callingPatient.id;
    	}
    	sendStatus(patientId, "TERMINATED");
    }
    
	/* Load file info from the server */
	
	function getSharedFiles() {
		
		if (typeof $rootScope.callingPatient !== "undefined") {
			$http.get(REST_API + "doctors/" + $rootScope.userDetails.id 
					+ "/reservations/" + $rootScope.callingPatient.reservation.id 
					+ "/sharedFiles")
					
			.then(function successCallback(response) {
				$scope.files = response.data;
				$scope.files.forEach(function (item) {
					item.uploadTime = TimeService.parseWithTimezone(item.uploadTime);
				});
				
			}, function errorCallback(response) {
				console.error("[ERROR]: Couldn't load shared files");
			});
		}
	}
    
    function initListeners(peerConnectionConfig) {

        /* A list of available STUN/TURN servers has been obtained
         * now we will create a Simple$scope.webrtc instantion based on
         * the received configuration data. */

        $scope.webrtc = new SimpleWebRTC({
            localVideoEl: "localVideo",
            remoteVideosEl: "remoteVideos",
            autoRequestMedia: true,
            debug: false,
            detectSpeakingEvents: true,
            autoAdjustMic: false,
            socketio: {'force new connection': true},

            // Add the new peerConnectionConfig object
            peerConnectionConfig: peerConnectionConfig
        });

    	$scope.$on('$locationChangeStart', function(event, oldUrl, newUrl) {
    		$scope.disconnect();
		});
        
    	$scope.webrtc.on("readyToCall", function () {
        	$scope.webrtc.joinRoom($scope.connectionDetails.room);
        	$scope.connected = true;
        	console.log("Connected to the consultation room");
        });

        $scope.webrtc.on("videoAdded", function (video, peer) {
            $("#videosSection")
                .css("width", "auto")
                .css("height", "auto");
            
            console.log("A new user has joined the room");
            updateVolumeLevel()
        });

        $scope.webrtc.on("localScreenRemoved", function (video) {
            $scope.disconnect();
        });
        
        $scope.webrtc.connection.on("message", function(message){
    		if (message.type === "chat") {
    			addMessage(message.payload.sender, message.payload.content, "btn-success");
    		}
        });
        
        $("#call-btn").click(function () {
        	if (!$scope.connected) {
        		$scope.connect();
        	}
        });

        $("#disconnect-btn").click(function () {
            $scope.disconnect();
        });
        
        $("#terminate-consultation-btn").click(function () {
        	terminatePrevConsultation();
        });

        $("#mute-btn").click(function () {
            if (remotes.volume == 0.0) {
            	remotes.volume = 0.5;
            	
                $scope.webrtc.unmute();
                $(this).removeClass("glyphicon-volume-off");
                $(this).addClass("glyphicon-volume-up");
                $("#volume-level-range").val(remotes.volume * 100);

            } else {
            	$scope.webrtc.mute();
            	remotes.volume = 0.0;
                $(this).removeClass("glyphicon-volume-up");
                $(this).addClass("glyphicon-volume-off");
                $("#volume-level-range").val(0);
            }
            
            $scope.webrtc.setVolumeForAll(remotes.volume);
        });

        /* Note that the input of the slider is an integer
         * between 0 and 100. */

        $("#volume-level-range").change(updateVolumeLevel);

        $("#screenshot-btn").click(function () {
        	var remoteVideo = $("#remoteVideos video")[0];
        	var canvas = document.createElement("canvas");
        	canvas.width = remoteVideo.videoWidth;
        	canvas.height = remoteVideo.videoHeight;
        	
        	var ctx = canvas.getContext("2d");
        	ctx.drawImage(remoteVideo, 0, 0);
        	
        	var link = $("<a></a>")
   	    		.attr("download", "screenshot_" + TimeService.now().format("DD-MM-YYYY[_]HH:mm") + ".png")
    			.attr("href", canvas.toDataURL("image/png"));
    	
        	link = link[0];
        	document.body.appendChild(link);
        	link.click();
        	document.body.removeChild(link);
        });

        $("#fullscreen-btn").click(function () {

            /* Note that we use the document.getElementById
             * function. It is necessary to obtain the video
             * element this way if we want to call the
             * *-RequestFullScreen function. */

            var video = document.getElementById("remoteVideos").children[0];
            var resizeFunction = video.requestFullscreen
                || video.webkitRequestFullScreen
                || video.mozRequestFullScreen
                || video.msRequestFullscreen;

            if (resizeFunction !== "undefined") {
                resizeFunction.call(video);
            }
        });
    }

    function setRemotePeerVideosEnabled(value) {
        var peers = $scope.webrtc.getPeers();

        for (var p in peers) {
            var streams = peers[p].pc.getRemoteStreams();

            for (var s in streams) {
                var tracks = streams[s].getTracks();
                for (var t in tracks) {
                    tracks[t].enabled = value;
                }
            }
        }
    }
    
    function startTransmission() {
        console.log("Transmission started");
        $scope.webrtc.unmute();
        $scope.webrtc.startLocalVideo();
    }

    function stopTransmission() {
		console.log("Transmission stopped");
		$scope.webrtc.mute();
		$scope.webrtc.stopLocalVideo();
    }
    
    function setAvailability(isAvailable) {
    	$http.post(REST_API + "doctors/" + $rootScope.userDetails.id + "/available/set/" + isAvailable)
    		.then(function successCallback(response) {
    			console.log("Availability status has been changed to: " + response.data);
    		
    		}, function errorCallback(response) {
    	    	console.error("[ERROR]: Couldn't change availability status");
    	    });
    }
    

    function updateVolumeLevel() {
    	var muteBtn = $("#mute-btn");
        remotes.volume = $("#volume-level-range").val() / 100.0;
        $scope.webrtc.setVolumeForAll(remotes.volume);
        
        if (remotes.volume > 0
        		&& muteBtn.hasClass("glyphicon-volume-off")) {
            $scope.webrtc.unmute();
            muteBtn.removeClass("glyphicon-volume-off");
            muteBtn.addClass("glyphicon-volume-up");

        } else if (remotes.volume == 0
        		&& muteBtn.hasClass("glyphicon-volume-up")) {
            $scope.webrtc.mute();
            muteBtn.removeClass("glyphicon-volume-up");
            muteBtn.addClass("glyphicon-volume-off");
        }
    }
    
    /* Text chat controls logic */
    
    function addMessage(sender, content, bgClass) {
    	var messageEl = $("<div></div>")
    		.addClass("message")
			.addClass(bgClass);
	
		var nameLabel = $("<span class='" + bgClass + " msg-label'></span>")
			.text(sender);
		
		messageEl.append($(nameLabel));
		var contentEl = $("<div class='msg-content'></div>")
			.text(content);
		
		messageEl.append($(contentEl));
		
		var chatPanel = $("#chat-messages");
		chatPanel.append($(messageEl));
		chatPanel.scrollTop(chatPanel[0].scrollHeight);
    }
    
    function sendTextMessage() {
		var textInput = $("#chat-input-text")
		var content = textInput.val();
		
		if (content.length > 0
				&& $scope.webrtc !== undefined) {
			
			$scope.webrtc.sendToAll("chat", {
				senderId: $rootScope.userDetails.id,
				sender: $rootScope.userDetails.firstName + " " + $rootScope.userDetails.lastName,
				content: content
			});
			addMessage($rootScope.userDetails.name, content, "btn-default");
			textInput.val("");
		}
	}
    
    function initControlls() {
    	$scope.showTab = function (tabId) {
    		$("#" + tabId + "-btn").removeClass("btn-default").addClass("btn-success")
    			.siblings()
    				.removeClass("btn-success")
    				.addClass("btn-default");
    		
    		$("#" + tabId).removeClass("collapse")
    			.siblings().addClass("collapse");
    	};
    	
    	$("#chat-input-text").keyup(function (e) {
    		var code = e.keyCode || e.which;
    		
    		// if enter
    		
    		if (code == 13) {
    			sendTextMessage();
    		}
    	});
    	
    	$("#chat-input-submit").click(sendTextMessage);
    }
    
    /* Current and previous reservation data controller */
    
    function getNextReservation(reservationId) {
		$http.get(REST_API + "doctors/" + $rootScope.userDetails.id + "/reservations/" + reservationId + "/next")
		    .then(function successCallback(response) {
		    	
		    	if (response.data.id > 0) {
		    		$rootScope.nextReservation = response.data;
		    		$rootScope.nextReservation.startDateTime = TimeService.parseWithTimezone($rootScope.nextReservation.startDateTime);
		    		$rootScope.nextReservation.endDateTime = TimeService.parseWithTimezone($rootScope.nextReservation.endDateTime);
		    	
		    	} else {
		    		$rootScope.nextReservation = null;
		    	}
		    	
		    }, function errorCallback(response) {
		    	alert("[ERROR]: Couldn't load current reservation data");
		    	$location.url("/login");
		    });
    }
    
    /*
     * Notes controller
     * */
    
    $scope.isNoteModified = function (noteId) {
    	return $scope.notesModificationState[noteId];
    };
    
    function getNotesForPatient(patientId) {
    	$http.get(REST_API + "doctors/" + $rootScope.userDetails.id + "/notes/patient/" + patientId)
		    .then(function successCallback(response) {
		    	$rootScope.notes = response.data;
		    	
		    }, function errorCallback(response) {
		    	alert("[ERROR]: Couldn't load notes");
		    	$location.url("/login");
		    });
    }
    
    $scope.addNote = function () {
    	if ($scope.newNote && $scope.newNote.content.length > 0) {
	    	$scope.newNote.patientId = $rootScope.callingPatient.id;
    		$scope.newNote.doctorId = $rootScope.userDetails.id;
    		$scope.newNote.content = $scope.newNote.content.trim();
	    		
	    	$http.post(REST_API + "doctors/" + $rootScope.userDetails.id + "/notes", 
	    			$scope.newNote)
			    .then(function successCallback(response) {
			    	$scope.newNote.content = "";
			    	getNotesForPatient($rootScope.callingPatient.id);
			    	
			    }, function errorCallback(response) {
			    	alert("[ERROR]: Couldn't load notes");
			    	$location.url("/login");
			    });
    	}
    }
    
    $scope.editNote = function (noteId) {
    	$scope.isNoteModified[noteId] = true;

    	var note = $("#note-" + noteId);
    	var textArea = $('<textarea id="note-text-input-' + noteId + '" rows="5" class="note btn btn-md col-md-12"></textarea>')
    	textArea.text(note.text().trim());
    	
    	textArea.insertAfter(note);
    	note.addClass("collapse");
    }
    
    $scope.cancelNoteModification = function (noteId) {
    	$scope.isNoteModified[noteId] = false;

    	$("#note-text-input-" + noteId).remove();
    	$("#note-" + noteId).removeClass("collapse");
    }
    
    $scope.updateNote = function (noteId) {
    	var newContent = $("#note-text-input-" + noteId).val().trim();
    	
    	if (newContent.length > 0) {
    		$scope.cancelNoteModification(noteId);
    		
    		var noteDetails = {
    				noteId: noteId,
    				patientId: $rootScope.callingPatient.id,
    				doctorId: $rootScope.userDetails.id,
    				content: newContent
    		};
    		
    		$http.put(REST_API + "doctors/" + $rootScope.userDetails.id + "/notes", 
    				noteDetails)
    				.then(function successCallback(response) {
    					$("#note-" + noteId).text(newContent);
    					
    				}, function errorCallback(response) {
    					alert("[ERROR]: Couldn't load notes");
    					$location.url("/login");
    				});
    	}
    	
    }
    
    $scope.deleteNote = function (noteId) {
    	$scope.isNoteModified[noteId] = false;
    	
    	$http.delete(REST_API + "doctors/" + $rootScope.userDetails.id + "/notes/" + noteId)
		    .then(function successCallback(response) {
		    	$rootScope.notes = $rootScope.notes.filter(function (item) {
		    		return item.id !== noteId;
		    	});
		    	
		    }, function errorCallback(response) {
		    	alert("[ERROR]: Couldn't load notes");
		    	$location.url("/login");
		    });
    }
}]);