



/**
 * WebSocket API, which only works once initialized
 */
const ws = {
		
	/**
	 * WebSocket, or null if none connected
	 */
	//socket: null,
	  socket: [],
	
	/**
	 * Sends a string to the server via the websocket.
	 * @param {string} text to send 
	 * @returns nothing
	 */
	send: (text) => {
		if (ws.socket.length > 0) {
			ws.socket[0].send(text);
		}
	},
	
	
	sendFriendshipRequestNotification: (text) =>{
		if (ws.socket.length > 0) {
			ws.socket[1].send(text);
		}
	},

	/**
	 * Default action when text is received. 
	 * @returns
	 */
	receive: (text) => {
		console.log(text);
	},
	
	
	
	receiveFriendshipRequest: (text) => {
		console.log(text);
	},
	/**
	 * Attempts to establish communication with the specified
	 * web-socket endpoint. If successfull, will call 
	 * @returns
	 */
	initialize: () => {
		try {
			ws.socket.push(new WebSocket("ws://localhost:8080/ws"));
			ws.socket.push(new WebSocket("ws://localhost:8080/friendship"));
			ws.socket[0].onmessage = (e) => ws.receive(e.data);
			ws.socket[1].onmessage = (e) => ws.receiveFriendshipRequest(e.data);
			//console.log("Connected to WS '" + endpoint + "'")
		} catch (e) {
			console.log("Error, connection to WS FAILED: ", e);
		}
	}
} 

/**
 * Actions to perform once the page is fully loaded
 */
window.addEventListener('load', () => {
	//document.querySelectorAll(".vote").forEach(e => addVoteListener(e));
	//document.querySelectorAll(".ask").forEach(e => addQuestionListener(e));
	//if (m3.socketUrl !== false) {
		//ws.initialize(m3.socketUrl);
	//}
	ws.initialize();
});