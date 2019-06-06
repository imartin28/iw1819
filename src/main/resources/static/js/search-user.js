
$(() => { 
	
	$(".btnFriendRequest").on("click", btnAddFriendShowModalHandler);
	
	
	
	
	$("#userSearchForm").submit(function() {
		let searchText = $("#userSearchInput").val();
		searchText = searchText.trim();
		return (searchText !== null && searchText !== "");
	});
	
});


function btnAddFriendShowModalHandler(){
	let emailUserReceiver = $(this).attr("data-user-receiver-email");
	
	$("#userReceiver").val(emailUserReceiver);
}