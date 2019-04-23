"use strict"

$(function() {
	
	$(".btnRemoveFriend").click(function() {
		let inputId = $(this).parent().find("input");
		let userId = $(inputId).val();
		let nickname = $(this).parent().find(".nick").text();
		
		if(userId !== null && userId !== "") {
			$("#userId").val(userId);
		}
		
		$("#userNameSpan").text(nickname);
		$("#modalRemoveFriend").modal('toggle');
	});
	
	$("#btnAccept").click(function(e) {
		return resolveFriendRequest(this, event, "true");
	});
	
	$("#btnReject").click(function(e) {
		return resolveFriendRequest(this, event, "false");
	});
	
});

function resolveFriendRequest(these, event, accepted) {

	event.preventDefault();
	
	$("#accept").val(accepted);
	let userId = $(these).parent().parent().parent().find(".userIdFriendRequest").val();
	let send = (userId !== null && userId !== "");
	
	if(send) {
		$(these).parent().find("#friendUserId").val(userId);
		$(these).parent().submit();
	}
	
	return send;
}