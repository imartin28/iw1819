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
    		console.log(map);
        	$.ajax({
    			type: "POST",
    			url: "/admin/users",
    			headers: {
    				"Content-Type": "application/json",				
    				"X-CSRF-TOKEN": m3.csrf.value
    			},
    			dataType : "json",
    		    contentType: "application/json; charset=utf-8",
    			data: map,
    			success: function(response) {
    				console.log("peticion ajax vuelve con exito");
    				console.log(response);
    				location.reload();
    			},
    			error:function (xhr, ajaxOptions, thrownError){
    				console.log(xhr.status);             
    				console.log(thrownError);     
    			} 
    		});
    	}
    }
    
    $("#activate-selected").click(function() {
    	handleModifyUserState(true);
    });
    $("#delete-selected").click(function() {
    	handleModifyUserState(false);
    });
    
    $('#fileTable').DataTable({
        select: true,
    });
    
    $("#remove-selected").click(function() {
    	let fileIds = {};
    	let rows = $('#fileTable').find("tr.selected");
    	for (let i = 0; i < rows.length; i++) {
    		fileIds['file' + $(rows[i]).attr("id").toString()] = "OK";
    	}
    	
    	let map = JSON.stringify(fileIds);
    	
    	if (map != null && map != "" && map != "{}") {
    		console.log(map);
        	$.ajax({
    			type: "POST",
    			url: "/admin/files",
    			headers: {
    				"Content-Type": "application/json",				
    				"X-CSRF-TOKEN": m3.csrf.value
    			},
    			dataType : "json",
    		    contentType: "application/json; charset=utf-8",
    			data: map,
    			success: function(response) {
    				console.log("peticion ajax vuelve con exito");
    				console.log(response);
    				location.reload();
    			},
    			error:function (xhr, ajaxOptions, thrownError){
    				console.log(xhr.status);             
    				console.log(thrownError);     
    			} 
    		});
    	}
    });
    
});