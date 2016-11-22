'use strict';

myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/doctor-consultation', {
        templateUrl: 'views/doctors/consultation/consultation.html',
        controller: 'ConsultationDoctorCtrl'
    });
}]);

myApp.controller('ConsultationDoctorCtrl', ['REST_API', "$rootScope", '$scope', '$http', '$location', 'UserService',
  function (REST_API, $rootScope, $scope, $http, $location, UserService) {
	$rootScope.userDetails = UserService.getUserOrRedirect($location, "/reservation");
	initControlls();
	
	$http.get(REST_API + "doctors/" + $rootScope.userDetails.id + "/connection")
	    .then(function successCallback(response) {
	    	$scope.connectionDetails = response.data;
	    	
	    	if ($scope.connectionDetails === undefined
	        		|| $scope.connectionDetails === null) {
	            alert("[ERROR]: Invalid connection data");
	            $location.url("/login");

	        } else {
	            peerConnectionConfig = getCredentials(initListeners);
	        }
	    	
	    }, function errorCallback(response) {
	    	alert("[ERROR]: Couldn't load connection data");
	    	$location.url("/login");
	    });
	
    $scope.webrtc = {};
    var remotes = {
       	 volume: 0.5
    };
    var peerConnectionConfig;
    
    /*
     * Rendering shared files
     * */
    
	$scope.sortField = "name";
	$scope.sortReversed = true;

	/* Load file info from the server */
	
	$scope.getSharedFiles = function() {
		$http.get(REST_API + "doctors/" + $rootScope.userDetails.id 
				+ "/reservations/" + $scope.callingPatient.reservation.id 
				+ "/sharedFiles")
				
		.then(function successCallback(response) {
			$scope.files = response.data;
			$scope.files.forEach(function (item) {
				item.uploadTime = new Date(item.uploadTime);
			});
			
		}, function errorCallback(response) {
			console.log("[ERROR]: " + response);
		});
	}
	
	/* 
	 * Connection logic
	 * */
    
    function connect() {
    	$scope.webrtc.joinRoom($scope.connectionDetails.room);
        setAvailability(true);
        startTransmission();
    }

    function sendCallRequestResponse(status) {
    	$scope.stompClient.send("/queue/patients/" + $scope.callingPatient.id + "/notifications",
			{},
			JSON.stringify({
				status: status
			}));
    }
    
    function subscribe(callback) {
    	var socket = new SockJS(REST_API + "ws")
		var stompClient = Stomp.over(socket);
    	$scope.stompClient = stompClient;
    	
    	stompClient.connect({}, function (frame) {
    		$scope.subscription = stompClient.subscribe("/queue/doctors/" + $rootScope.userDetails.id + "/calling", function (message) {
				$scope.callingPatient = JSON.parse(message.body);
				
				var dialog = $("#calling-modal");
				$("#calling-patient-id").text($scope.callingPatient.id);
				$("#calling-patient-name").text($scope.callingPatient.name);
				
				var startTime = new Date($scope.callingPatient.reservation.startDateTime);
				startTime = "" + ((startTime.getHours() > 9) ? startTime.getHours() : "0" + startTime.getHours()) 
					+ ":" + ((startTime.getMinutes() > 9) ? startTime.getMinutes() : "0" + startTime.getMinutes());
				
				$("#calling-patient-start").text(startTime);
				
				$("#accept-btn").click(function () {
					sendCallRequestResponse("ACCEPTED");
					$scope.getSharedFiles();
					dialog.modal("hide");
//					ringTone.pause();
				});
				$("#reject-btn").click(function () {
					sendCallRequestResponse("REJECTED");
					dialog.modal("hide");
//					ringTone.pause();
				});
				
				dialog.modal("show");
    		});
    		
    		callback();
    	});
    }
    
    function initListeners(peerConnectionConfig) {

        /* A list of available STUN/TURN servers has been obtained
         * now we will create a Simple$scope.webrtc instantion based on
         * the received configuration data. */

        $scope.webrtc = new SimpleWebRTC({
            localVideoEl: "localVideo",
            remoteVideosEl: "remoteVideos",
            autoRequestMedia: false,
            debug: false,
            detectSpeakingEvents: true,
            autoAdjustMic: false,

            // Add the new peerConnectionConfig object
            peerConnectionConfig: peerConnectionConfig
        });

    	$scope.$on('$routeChangeStart', function(next, current) { 
    		disconnect();
		});
        
        $scope.webrtc.on("readyToCall", function () {
            console.log("Connected to [" + $scope.connectionDetails.room + "]");
        });

        $scope.webrtc.on("videoAdded", function (video, peer) {
            $("#videosSection")
                .css("border", "none")
                .css("width", "auto")
                .css("height", "auto");
            console.log("" + peer + " has joined the room");
            updateVolumeLevel()
        });

        /* disconnect and leave the room */

        $scope.webrtc.on("localScreenRemoved", function (video) {
            disconnect();
        });
        
        $scope.webrtc.connection.on("message", function(message){
    		if (message.type === "chat") {
    			addMessage(message.payload.sender, message.payload.content, "bg-primary");
    		}
        });
        
        $("#call-btn").click(function () {
            $(this).prop("disabled", true);
            $(this).addClass("disabled");
            $("#disconnect-btn").prop("disabled", false);
            $("#disconnect-btn").removeClass("disabled");
            
            subscribe(connect);
        });

        $("#disconnect-btn").click(function () {
            $(this).prop("disabled", true);
            $("#call-btn").prop("disabled", false);
            disconnect();
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

    function getCredentials(callback) {
        var peerConnectionConfig;
        console.log("Attempting to connect to the room with ID: ["
            + $scope.connectionDetails.room + "]...");
      
        $.ajax({
            url: $scope.connectionDetails.iceEndpoint,
            data: $scope.connectionDetails,
            success: function (data, status) {
            	console.log(data);
                peerConnectionConfig = data.d;

                /* data.d is where the iceServers object lives */

                if (!peerConnectionConfig) {
                    alert("[ERROR]: Connection attempt has failed");
                    
                } else {
                    callback(peerConnectionConfig);
                }
            },
            error: function () {
            	console.log("[ERROR]: TURN server error");
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
        if ($scope.webrtc !== undefined) {
            console.log("Transmission started");
            $scope.webrtc.unmute();
            $scope.webrtc.startLocalVideo();
            setRemotePeerVideosEnabled(true);
            setAvailability(true);
        }
    }

    function setAvailability(isAvailable) {
    	$http.post(REST_API + "doctors/" + $rootScope.userDetails.id + "/available/set/" + isAvailable)
    		.then(function successCallback(response) {
    			console.log("Availability status has been changed to: " + response.data);
    		
    		}, function errorCallback(response) {
    	    	console.log("[ERROR]: Couldn't change availability status");
    	    });
    }
    
    function stopTransmission() {
        if ($scope.webrtc !== "undefined") {
            console.log("Transmission stopped");
            $scope.webrtc.mute();
            $scope.webrtc.stopLocalVideo();
            setRemotePeerVideosEnabled(false);
        }
    }

    function disconnect() {
        if ($scope.subscription && $scope.webrtc !== "undefined") {
        	setAvailability(false);
        	$scope.subscription.unsubscribe();
        	$scope.stompClient.disconnect();
            stopTransmission();
            $scope.webrtc.leaveRoom();
            
            console.log("Disconnected from: [" + $scope.connectionDetails.room + "]");
        }
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
			
			console.log(content);
			$scope.webrtc.sendToAll("chat", {
				senderId: $rootScope.userDetails.id,
				sender: $rootScope.userDetails.firstName + " " + $rootScope.userDetails.lastName,
				content: content
			})
			addMessage($rootScope.userDetails.name, content, "btn-success");
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
    
}]);