var webrtc = {};
var app = {
	initialized: false
};

$(document).ready(function () {
    var peerConnectionConfig;
    var roomId = $("meta[name=roomId]").attr("content");
    
    console.log(roomId);
    
    if (roomId === null) {
        console.log("Connection cancelled");
        return;
        
    } else {
    	
        peerConnectionConfig = getCredentials(roomId);
        
        if (!peerConnectionConfig) {
            console.log("Connection attempt has failed");
            alert("Invalid room id");
        }
    }

    /* list of available STUN/TURN servers has been obtained
     * now we will create a SimpleWebRTC instantion based on
     * the received configuration data. */
    
    webrtc = new SimpleWebRTC({
        localVideoEl: "localVideo",
        remoteVideosEl: "remoteVideos",
        autoRequestMedia: true,
        debug: false,
        detectSpeakingEvents: true,
        autoAdjustMic: false,
    
        // Add the new peerConnectionConfig object
        peerConnectionConfig: peerConnectionConfig
    });

    webrtc.on("localStream", function () {
        $("#call-btn").prop("disabled", false);
    });
    
    webrtc.on("readyToCall", function () {
        console.log("Connected to [" + roomId + "]");
    });
    
    webrtc.on("videoAdded", function (video, peer) {
        $("#videosSection")
            .css("border", "none")
            .css("width", "auto")
            .css("height", "auto");
    	console.log("" + peer + " has joined the room");
    });
    
    /* disconnect and leave the room */
    
    webrtc.on("localScreenRemoved", function (video) {
    	$("#localVideo").css("background-color", "white");
    	disconnect(webrtc, roomId);
    });
    
    $("#call-btn").click(function () {
    		
    		if (!app.initialized) {
    			webrtc.joinRoom(roomId);
    			app.initialized = true;
    		}
    		
    		$(this).prop("disabled", true);
    		$("#hang-btn").prop("disabled", false);
    		$("#disconnect-btn").prop("disabled", false);
    		startTransmission(webrtc);
    });
    
    $("#hang-btn").click(function () {
    		$("#call-btn").prop("disabled", false);
    		$(this).prop("disabled", true);
    		stopTransmission(webrtc);
    });
    
    $("#disconnect-btn").click(function () {
    		$("#disconnect-btn").prop("disabled", true);
    		$("#hang-btn").prop("disabled", true);
    		disconnect(webrtc, roomId);
    });
    
    $("#mute-btn").click(function () {
    	var video = document.getElementById("localVideo");
    	
    	if (video.volume === 0) {
    		video.volume = 0.5;
    		webrtc.unmute();
    		$(this).removeClass("glyphicon-volume-off");
    		$(this).addClass("glyphicon-volume-up");
    		$("#volume-level-range").val(video.volume * 100);
  
    	} else {
    		video.volume = 0;
    		webrtc.mute();
      		$(this).removeClass("glyphicon-volume-up");
    		$(this).addClass("glyphicon-volume-off");
    		$("#volume-level-range").val(0);
    	}
    });
    
    /* Note that the input of the slider is an integer
     * between 0 and 100. */
    
    $("#volume-level-range").change(function () {
    	var video = document.getElementById("localVideo");
    	video.volume = $(this).val() / 100.0;
    	console.log("Volume changed to: " + video.volume);
    	var muteBtn = $("#mute-btn");
    	
    	if (video.volume > 0 
    			&& muteBtn.hasClass("glyphicon-volume-off")) {
    		webrtc.unmute();
    		muteBtn.removeClass("glyphicon-volume-off");
    		muteBtn.addClass("glyphicon-volume-up");
    		
    	} else if (video.volume == 0 
    			&& muteBtn.hasClass("glyphicon-volume-up")) {
    		webrtc.mute();
    		muteBtn.removeClass("glyphicon-volume-up");
    		muteBtn.addClass("glyphicon-volume-off");
    	}
    });
    
    $("#screenshot-btn").click(function () {
    });
    
    $("#fullscreen-btn").click(function () {
    	
    	/* Note that we use the document.getElementById 
    	 * function. It is necessary to obtain the video 
    	 * element this way if we want to call the 
    	 * *-RequestFullScreen function. */
    	
    	var video = document.getElementById("localVideo");
    	var resizeFunction = video.requestFullscreen
			|| video.webkitRequestFullScreen
			|| video.mozRequestFullScreen
			|| video.msRequestFullscreen;
    	
    	if (resizeFunction !== "undefined") {
    		resizeFunction.call(video);
    	}
    });

});

function getCredentials(roomId) {
    var peerConnectionConfig;
    console.log("Attempting to connect to the room with ID: ["
        + roomId + "]...");
    
    $.ajax({
        url: "https://service.xirsys.com/ice",
        data: {
            ident: "medcontact",
            secret: "3c68aea0-2db0-11e6-b5a7-1017d1173ceb",
            domain: "www.medcontact.com",
            application: "medcontact",
            room: roomId,
            secure: 1
        },
        success: function (data, status) {
        	
            /* data.d is where the iceServers object lives */
        	
            peerConnectionConfig = data.d;
            console.log(peerConnectionConfig);
        },
        async: false
    });
    
    return peerConnectionConfig;
}

function toggleEnabled(element) {
    if (element.hasClass("enabled")) {
        element.removeClass("enabled");
        element.addClass("disabled");
        
    } else if (element.hasClass("disabled")) {
        element.removeClass("disabled");
        element.addClass("enabled");
    }
}

function setRemotePeerVideosEnabled(webrtc, value) {
	var peers = webrtc.getPeers();
	
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

function startTransmission(webrtc) {
	if (webrtc !== "undefined") {
		console.log("Transmission started");
        webrtc.unmute();
        webrtc.startLocalVideo();
        setRemotePeerVideosEnabled(webrtc, true);
	}
}

function stopTransmission(webrtc) {
	if (webrtc !== "undefined") {
		console.log("Transmission stopped");
        webrtc.mute();
        webrtc.stopLocalVideo();
        setRemotePeerVideosEnabled(webrtc, false);
	}
}

function disconnect(webrtc, roomId) {
	if (webrtc !== "undefined") {
		stopTransmission(webrtc);
		webrtc.leaveRoom();
        
		console.log("Disconnected from: [" + roomId + "]");
	}
}