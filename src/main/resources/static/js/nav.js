"use strict"
$(function() {
	
	$("#btnTogglePlayer").click(function() {
		let icon = $(this).find("i");
	    if($(icon).text() === "keyboard_arrow_right") {//hide
	    	$(icon).text("keyboard_arrow_left");
	    	$(this).css("margin-left", "-20px");
	    }
	    else {//show
	    	$(icon).text("keyboard_arrow_right");
	    	$(this).css("margin-left", "-8px");
	    }
	    
	    let sidebar = $("#btnTogglePlayer").parent();
	    
	    let divPlayer = $(sidebar.find("div")[0]);
	    if($(divPlayer).hasClass("d-none"))
	    	$(divPlayer).removeClass("d-none");
	    else
	    	$(divPlayer).addClass("d-none");
	    
	    if(sidebar.hasClass("col-3")) {
	    	sidebar.removeClass("col-3");
	    	sidebar.addClass("col-0");
	    }
	    else {
	    	sidebar.removeClass("col-0");
	    	sidebar.addClass("col-3");
	    }
	});
	
	if($("#msg").text() && $("#msg").text() != "") {
		$("#msgModal").modal("toggle");
	}
	
	$("#msg").change(function() {
		$("#msgModal").modal("toggle");
	});
	
	/**
	 * WebSocket API, which only works once initialized
	 */
	const ws = {
			
		/**
		 * WebSocket, or null if none connected
		 */
		socket: null,
		
		/**
		 * Sends a string to the server via the websocket.
		 * @param {string} text to send 
		 * @returns nothing
		 */
		send: (text) => {
			if (ws.socket != null) {
				ws.socket.send(text);
			}
		},

		/**
		 * Default action when text is received. 
		 * @returns
		 */
		receive: (text) => {
			console.log(text);
		},
		
		/**
		 * Attempts to establish communication with the specified
		 * web-socket endpoint. If successfull, will call 
		 * @returns
		 */
		initialize: (endpoint) => {
			try {
				ws.socket = new WebSocket(endpoint);
				ws.socket.onmessage = (e) => ws.receive(e.data);
				console.log("Connected to WS '" + endpoint + "'")
			} catch (e) {
				console.log("Error, connection to WS '" + endpoint + "' FAILED: ", e);
			}
		}
	} 
	
	if (m3.socketUrl !== false) {
		ws.initialize(m3.socketUrl);
	}
	
});