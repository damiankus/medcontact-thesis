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

	if (typeof $rootScope.reservation === "undefined"
			|| $rootScope.reservation == null) {
		
		$location.url("/reservation");
		$scope.$apply();
	}
	
	$http.get(REST_API + "patients/" + $rootScope.userDetails.id + "/connection/" + $rootScope.reservation.id)
		.then(function successCallback(response) {
			$scope.connectionDetails = response.data;
			
			if (typeof $scope.connectionDetails === "undefined"
					|| $scope.connectionDetails == null) {
				alert("[ERROR]: Invalid connection data");
				$location.url("/login");
				$scope.$apply();
				
			} else {
				getCredentials();
			}
			
		}, function errorCallback(response) {
			alert("[ERROR]: Couldn't load connection data");
			$location.url("/login");
		});
	
	initTabControls();
	
    $scope.webrtc = {};
    $scope.connected = false;
    var remotes = {
    	 volume: 0.5
    };
    
    function subscribeForNotifications(callback) {
    
    	var socket = new SockJS(REST_API + "ws")
		var stompClient = Stomp.over(socket);
    	$scope.stompClient = stompClient;
    	
    	stompClient.connect({}, function (frame) {
    		$scope.subscription = stompClient.subscribe("/queue/patients/" + $rootScope.userDetails.id + "/notifications", function (message) {
				var response = JSON.parse(message.body);
    			
				if (response.status == "TERMINATED") {
					var dialog = $("#modal-call-status");
					disconnect();
					
					$("#accept-btn").one("click", function () {
						dialog.modal("hide");
						
						setTimeout(function () {
							$location.path("/reservation");
							$scope.$apply();
						}, 1000);
					});
					
					dialog.modal("show");
				}
    		}, 
    		{
    			id: $rootScope.userDetails.id
    		});
    		
    		callback();
    	});
    }
    
    function getCredentials() {
        var peerConnectionConfig;
        console.log("Attempting to connect to the room with ID: ["
            + $scope.connectionDetails.room + "]...");
      
        $.ajax({
            url: $scope.connectionDetails.iceEndpoint,
            data: $scope.connectionDetails,
            success: function (data, status) {
                peerConnectionConfig = data.d;

                /* data.d is where the iceServers object lives */

                if (!peerConnectionConfig) {
                    console.log("[ERROR]: Connection attempt has failed");
                    
                } else {
                    subscribeForNotifications(notifyDoctor);
                    initListeners(peerConnectionConfig);
                }
            },
            error: function () {
            	console.log("[ERROR]: TURN server error");
            }
        });
    }
    
    function connect() {
    	$scope.connected = true;
    	console.log("Connecting to [" + $scope.connectionDetails.room + "]");
    	startTransmission();
    }
    
    function disconnect() {
    	$scope.connected = false;
        stopTransmission();
        $scope.webrtc.leaveRoom();
        console.log("Disconnected from: [" + $scope.connectionDetails.room + "]");
    }
    
    function notifyDoctor() {
    	$scope.stompClient.send("/queue/doctors/" + $rootScope.reservation.doctorId + "/calling", 
        		{}, JSON.stringify({
        			id: $rootScope.userDetails.id,
        			name: $rootScope.userDetails.firstName + " " + $rootScope.userDetails.lastName,
        			reservation: $rootScope.reservation
        		}));
    }
    
    function initListeners(peerConnectionConfig) {

        /* list of available STUN/TURN servers has been obtained
         * now we will create a SimpleWebRTC instantion based on
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
        
    	$scope.$on('$routeChangeStart', function(next, current) { 
    		disconnect();
		});

        $scope.webrtc.on("readyToCall", function () {
        	$scope.webrtc.joinRoom($scope.connectionDetails.room);
        	$scope.connected = true;
            console.log("Connected to [" + $scope.connectionDetails.room + "]");
        });

        $scope.webrtc.on("videoAdded", function (video, peer) {
            $("#videosSection")
                .css("width", "auto")
                .css("height", "auto");
            console.log("A new user has joined the room");
            updateVolumeLevel();
            
        });

        /* disconnect and leave the room */

        $scope.webrtc.on("localScreenRemoved", function (video) {
            disconnect();
        });
        
        $scope.webrtc.connection.on("message", function(message){
    		if (message.type === "chat") {
    			addMessage(message.payload.sender, message.payload.content, "btn-success");
    		}
        });
  
        $("#call-btn").click(function () {
            $("#call-btn").prop("disabled", false);
            notifyDoctor();
            
            if (!$scope.connected) {
            	connect();
            }
        });
        
        $("#disconnect-btn").click(function () {
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
        	var remoteVideo = $("#remoteVideos video")[0];
        	var canvas = document.createElement("canvas");
        	
        	canvas.width = remoteVideo.videoWidth;
        	canvas.height = remoteVideo.videoHeight;
        	
        	var ctx = canvas.getContext("2d");
        	ctx.drawImage(remoteVideo, 0, 0);
        	var now = new Date();
        	
        	var link = $("<a></a>")
	    		.attr("download", "screenshot_" + (formatDate(now) + "_" + formatTime(now)) + ".png")
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

            if (resizeFunction !== undefined) {
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
        if (typeof $scope.webrtc !== "undefined") {
            console.log("Transmission started");
            $scope.webrtc.unmute();
            $scope.webrtc.startLocalVideo();
        }
    }

    function stopTransmission() {
        if (typeof $scope.webrtc !== "undefined") {
            console.log("Transmission stopped");
            $scope.webrtc.mute();
            $scope.webrtc.stopLocalVideo();
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
				&& typeof $scope.webrtc !== "undefined") {
			
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
				
			}, function errorCallback(response) {
				alert("[ERROR]: Couldn't load doctor info");
				$location.url("/login");
			});
    }
    
    function formatDate(dateTime) {
    	var date = new Date(dateTime);
    	return  "" + date.getDate() + "-" + date.getMonth() + "-" + date.getYear();
    }
    
    function formatTime(dateTime) {
    	var time = new Date(dateTime);
		return  "" + ((time.getHours() > 9) ? time.getHours() : "0" + time.getHours()) 
			+ ":" + ((time.getMinutes() > 9) ? time.getMinutes() : "0" + time.getMinutes());
    }
    
}]);