
$(() => { 

	$("#userSearchForm").submit(function() {
		let searchText = $("#userSearchInput").val();
		searchText = searchText.trim();
		return (searchText !== null && searchText !== "");
	});
	
});