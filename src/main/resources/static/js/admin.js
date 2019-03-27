"use strict"
$(function() {
    $('#userTable').DataTable({
        select: true,
    });
    
    $("#delete-selected").click(function() {
    	let userIdsToDelete = [];
    	let rows = $('#userTable').find("tr.selected");
    	for(let i = 0; i < rows.length; i++) {
    		userIdsToDelete.push($(rows[i]).attr("id"));
    	}
    	
    	let jsonArray = JSON.stringify(userIdsToDelete);
    	
    	if(userIdsToDelete && userIdsToDelete.length > 0) {
        	$.ajax({
    			type: "POST",
    			url: "/admin/delete-users",
    			headers: {
    				"Content-Type": "application/json",				
    				"X-CSRF-TOKEN": m3.csrf.value
    			},
    			dataType: 'json',
    			contentType:'application/json',
    			data: jsonArray,
    			success: function() {
    				console.log("users deleted");
    			},
    		});
    	}
    });
});