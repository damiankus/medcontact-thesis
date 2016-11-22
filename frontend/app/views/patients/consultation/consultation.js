'use strict';

myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/patient-consultation', {
        templateUrl: 'views/patients/consultation/consultation.html',
        controller: 'ConsultationPatientCtrl'
    });
}]);

myApp.controller('ConsultationPatientCtrl', ['REST_API', "$rootScope", '$scope', '$http', '$location', 'UserService',
  function (REST_API, $rootScope, $scope, $http, $location, UserService) {
	
	$rootScope.userDetails = UserService.getUserOrRedirect($location, "/reservation");

	if ($rootScope.reservation == undefined
			|| $rootScope.reservation == null) {
		$location.url("/reservation");
	}
	
	$http.get(REST_API + "patients/" + $rootScope.userDetails.id + "/connection/" + $rootScope.reservation.id)
		.then(function successCallback(response) {
			$scope.connectionDetails = response.data;
			
			if ($scope.connectionDetails === undefined
					|| $scope.connectionDetails === null) {
				alert("[ERROR]: Invalid connection data");
				$location.url("/login");
				
			} else {
				$("#call-btn").click(function () {
					$(this).prop("disabled", true);
					
					setTimeout(function () {
						$(this).prop("disabled", false);
					}, 5000);
					subscribe(notifyDoctor);
				});
				peerConnectionConfig = getCredentials(initListeners);
			}
			
		}, function errorCallback(response) {
			alert("[ERROR]: Couldn't load connection data");
			$location.url("/login");
		});
	
	initTabControls();
	
    $scope.webrtc = {};
    var remotes = {
    	 volume: 0.5
    };
    var peerConnectionConfig;
    
    function notifyDoctor() {
    	$scope.stompClient.send("/queue/doctors/" + $rootScope.reservation.doctorId + "/calling", 
        		{}, JSON.stringify({
        			id: $rootScope.userDetails.id,
        			name: $rootScope.userDetails.firstName + " " + $rootScope.userDetails.lastName,
        			reservation: $rootScope.reservation
        		}));
    }
    
    function subscribe(callback) {
    	var socket = new SockJS(REST_API + "ws")
		var stompClient = Stomp.over(socket);
    	$scope.stompClient = stompClient;
    	
    	stompClient.connect({}, function (frame) {
    		$scope.subscription = stompClient.subscribe("/queue/patients/" + $rootScope.userDetails.id + "/notifications", function (message) {
				var response = JSON.parse(message.body);
    			
				if (response.status == "ACCEPTED") {
	                $("#call-btn").prop("disabled", true);
	                $("#disconnect-btn").prop("disabled", false);
	                
	    			$scope.webrtc.joinRoom($scope.connectionDetails.room);
	    			startTransmission();
	    			
				} else if (response.status == "REJECTED") {
					console.log("[ERROR]: Call request has been rejected");
				}
    		});
    		
    		callback();
    	});
    }

    function initListeners(peerConnectionConfig) {

        /* list of available STUN/TURN servers has been obtained
         * now we will create a SimpleWebRTC instantion based on
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
    		disconnect($scope.webrtc, $scope.connectionDetails);
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
            updateVolumeLevel();
        });

        /* disconnect and leave the room */

        $scope.webrtc.on("localScreenRemoved", function (video) {
            $("#localVideo").css("background-color", "white");
            disconnect($scope.webrtc, $scope.connectionDetails);
        });
        
        $scope.webrtc.connection.on("message", function(message){
    		if (message.type === "chat") {
    			addMessage(message.payload.sender, message.payload.content, "btn-success");
    		}
        });
        
        $("#disconnect-btn").click(function () {
            $(this).prop("disabled", true);
            $("#call-btn").prop("disabled", false);
            disconnect($scope.webrtc, $scope.connectionDetails);
        });
        
        $("#mute-btn").click(function () {

            if (remotes.volume == 0.0) {
            	remotes.volume = 0.5;
                $scope.webrtc.unmute();
                $(this).removeClass("glyphicon-volume-off");
                $(this).addClass("glyphicon-volume-up");
                $("#volume-level-range").val(remotes.volume * 100);

            } else {
            	remotes.volume = 0.0;
                $scope.webrtc.mute();
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

            if (resizeFunction !== undefined) {
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
                    console.log("[ERROR]: Connection attempt has failed");
                    
                } else {
                    console.log(peerConnectionConfig);
                    callback(peerConnectionConfig);
                }
            },
            error: function () {
            	console.log("[Error]: TURN server error");join
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
            setRemotePeerVideosEnabled($scope.webrtc, true);
        }
    }

    function stopTransmission() {
        if ($scope.webrtc !== undefined) {
            console.log("Transmission stopped");
            $scope.webrtc.mute();
            $scope.webrtc.stopLocalVideo();
            setRemotePeerVideosEnabled($scope.webrtc, false);
        }
    }

    function disconnect() {
        if ($scope.webrtc !== undefined) {
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
			textInput.val("");
			addMessage($rootScope.userDetails.name, content, "btn-default");
		}
	}
    
    function initTabControls() {
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
    	
    	/* 
    	 * Load doctor info
    	 *  */
    	
    	$http.get(REST_API + "doctors/" + $rootScope.reservation.doctorId + "/info")
			.then(function successCallback(response) {
				$scope.doctorInfo = response.data;
				console.log(response.data);
				
			}, function errorCallback(response) {
				alert("[ERROR]: Couldn't load doctor info");
				$location.url("/login");
			});
    }
    
}]);