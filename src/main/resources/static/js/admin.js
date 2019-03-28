"use strict"
$(function() {
    $('#userTable').DataTable({
        select: true,
    });

    function handleModifyUserState(active) {
    	let userIds = {};
    	let rows = $('#userTable').find("tr.selected");
    	for(let i = 0; i < rows.length; i++) {
    		userIds['user'+$(rows[i]).attr("id").toString()] = active.toString();
    	}
    	
    	let map = JSON.stringify(userIds);
    	
    	if(map != null && map != "" && map != "{}") {
        	$.ajax({
    			type: "POST",
    			url: "/admin/users",
    			headers: {
    				"Content-Type": "application/json",				
    				"X-CSRF-TOKEN": m3.csrf.value
    			},
    			dataType: 'json',
    			data: map
    		});
    	}
    }
    
    $("#activate-selected").click(function() {
    	handleModifyUserState(true);
    });
    $("#delete-selected").click(function() {
    	handleModifyUserState(false);
    });
    
});