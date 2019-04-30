$(() => {

    $("#view-list").click(function() {
        $("#action-button-container").removeClass("d-none");
    });

    $("#view-files").click(function() {
        $("#action-button-container").addClass("d-none");
    });

    $("#edit-folder-view").click(function() {
        console.log("folderView "+(($("#folderView").is(":visible")) ? "visible" : "hidden"));
        if($("#folderView").is(":visible")) {
            console.log("action-button-container "+(($("#action-button-container").is(":visible")) ? "visible" : "hidden"));
            if(($("#action-button-container").is(":visible"))) {
                $("#action-button-container").addClass("d-none");
            }
            else {
                $("#action-button-container").removeClass("d-none");
            }
        }
    });
    
    $("#btn-new-tag").click(function() {
    	$("#modalNewTagLabel").text("Add new Tag");
    	$("#labelTagName").text("Introduce the name of the new tag:");
    	$("#nestTagLabel").parent().show();
    	$("#new-tag-isPlaylist").val("false");
    	$("#btn-create-tag-form").text("Create Tag");
    });
    
    $("#btn-new-playslist").click(function() {
    	$("#modalNewTagLabel").text("Add new Playlist");
    	$("#labelTagName").text("Introduce the name of the new playlist:");
    	$("#nestTagLabel").parent().hide();
    	$("#new-tag-isPlaylist").val("true");
    	$("#btn-create-tag-form").text("Create Playlist");
    });
    
    $(".btn-delete-playslist").click(function() {
    	
    });
    
    $(".btn-edit-playslist").click(function() {
    	
    });
});

