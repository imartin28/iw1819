"use strict"
$(function() {

    $(".playable").click(function() {
    	
    	let id = $(this).attr("data-fileId");	
    	
    	if(typeof id !== 'undefined' && id != null && id.length > 0) {
    		$.ajax({
    			type: "GET",
    			url: "/file/file/"+id,
    			headers: {		
    				"X-CSRF-TOKEN": m3.csrf.value
    			},
    			success: function(response) {
    				console.log("peticon ajax vuelve con exito");
    				console.log(response);
    			},
    			error:function (xhr, ajaxOptions, thrownError){
    				console.log(xhr.status);             
    				console.log(thrownError);     
    			} 
    		});
    	}
    	else {
    		id = $(this).attr("data-playlistId");
    		
    		if(typeof id !== 'undefined' && id != null && id.length > 0) {
        		$.ajax({
        			type: "GET",
        			url: "/file/tag/"+id,
        			headers: {				
        				"X-CSRF-TOKEN": m3.csrf.value
        			},
        			success: function(response) {
        				console.log("peticon ajax vuelve con exito");
        				console.log(response);
        			},
        			error:function (xhr, ajaxOptions, thrownError){
        				console.log(xhr.status);             
        				console.log(thrownError);     
        			} 
        		});
        	}
    	}
    });
    
});