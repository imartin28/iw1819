"use strict"

$(function() {

	$(".btnFriendRequest").click(function() {
		let inputId = $(this).parent().find("input");
		let userId = $(inputId).val();
		let nickname = $(this).parent().find(".nick").text();
		
		if(userId !== null && userId !== "") {
			$("#userId").val(userId);
		}
		
		$("#userNameSpan").text(nickname);
		$("#modalFriendRequest").modal('toggle');
	});
	
});