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
    				handleFile(JSON.parse(response));
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
        				handlePlaylist(JSON.parse(response));
        			},
        			error:function (xhr, ajaxOptions, thrownError){
        				console.log(xhr.status);             
        				console.log(thrownError);     
        			} 
        		});
        	}
    	}
    });
    
    function handleFile(file) {
    	$("#player-title").attr("data-id", file.id);
    	$("#player-title > a").attr("href", file.url);
    	$("#player-title > a > span").text(file.name);
    	
    	let type = file.mimetype.split("/")[0];
    	if(type === "image") {
    		$("#player-title > a > i:contains('image')").removeClass("d-none");
    		$("#player-title > a > i:contains('music_video')").addClass("d-none");
    		$("#player-title > a > i:contains('videocam')").addClass("d-none");
    		$("#mediaplayer").find("img").removeClass("d-none");
    		$("#mediaplayer").find("audio").addClass("d-none");
    		$("#mediaplayer").find("video").addClass("d-none");
    		$("#mediaplayer").find("img").attr("src", file.url);
    	}
    	else if(type === "audio") {
    		$("#player-title > a > i:contains('image')").addClass("d-none");
    		$("#player-title > a > i:contains('music_video')").removeClass("d-none");
    		$("#player-title > a > i:contains('videocam')").addClass("d-none");
    		$("#mediaplayer").find("img").addClass("d-none");
    		$("#mediaplayer").find("audio").removeClass("d-none");
    		$("#mediaplayer").find("video").addClass("d-none");
    		let source = $("#mediaplayer").find("audio > source");
    		$(source).attr("type", file.mimetype);
    		$(source).attr("src", file.url);
    		source.load();
    	}
    	else if(type === "video") {
    		$("#player-title > a > i:contains('image')").addClass("d-none");
    		$("#player-title > a > i:contains('music_video')").addClass("d-none");
    		$("#player-title > a > i:contains('videocam')").removeClass("d-none");
    		$("#mediaplayer").find("img").addClass("d-none");
    		$("#mediaplayer").find("audio").addClass("d-none");
    		$("#mediaplayer").find("video").removeClass("d-none");
    		let source = $("#mediaplayer").find("video > source");
    		$(source).attr("type", file.mimetype);
    		$(source).attr("src", file.url);
    		source.load();
    	}
    	
    	if($(".playlist-container").hasClass("d-flex")) {
    		$(".playlist-container").removeClass("d-flex");
    		$(".playlist-container").addClass("d-none");
    	}
    	
    	if($($("#btnTogglePlayer").siblings()[0]).hasClass("d-none")) {
    		$("#btnTogglePlayer").click();
    	}
    }
    
    function handlePlaylist(playlist){
    	$("#playlist-container").show();
    	$("#playlist-title > span").text(playlist.name);
    	$("#playlist-title").attr("data-playlistId", playlist.id);
    	
    	if(typeof playlist.files !== 'undefined' && playlist.files != null) {
    		for(let i = 0; i < playlist.files.length; i++) {
    			let playlistItem = $("#playlist-item-clone").clone();
    			$(playlistItem).show();
    			$(playlistItem).attr("id", playlist.files[i].id);
    			$(playlistItem).find(".info-title").text(playlist.files[i].name);
    			
    			let href = $(playlistItem).find(".info-title").attr("data-href");
    			$(playlistItem).find(".info-title").attr("data-href", href+playlist.files[i].id);
    			
    			$("#playlist-items").append(playlistItem);
    		}
    		
    		handleFile(playlist.files[0]);
    	}
    	
    	
    }
    
});