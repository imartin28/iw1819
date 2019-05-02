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
    	$("#new-tag-name").val("");
    	$("#nestTagLabel").parent().show();
    	$("#new-tag-isPlaylist").val("false");
    	$("#btn-create-tag-form").text("Create Tag");
    });
    
    $("#btn-new-playlist").click(function() {
    	$("#modalNewTagLabel").text("Add new Playlist");
    	$("#labelTagName").text("Introduce the name of the new playlist:");
    	$("#new-tag-name").val("");
    	$("#nestTagLabel").parent().hide();
    	$("#new-tag-isPlaylist").val("true");
    	$("#btn-create-tag-form").text("Create Playlist");
    });
    
    $(".btn-delete-playlist").click(function() {
    	
    });
    
    $(".btn-edit-tag").click(function() {
    	$("#modalEditTagLabel").text("Edit a Tag");
    	$("#labelTagNameEdit").text("Introduce the name of the new tag:");
    	$("#edit-tag-isPlaylist").val("true");
    });
    
    $(".btn-edit-playlist").click(function() {
    	let color = $(this).parent().parent().find("i.material-icons:contains('playlist_play')").attr("data-color");
    	$("#edit-tag-color").val(color);
    	$("#edit-tag-name").val($(this).parent().parent().find(".tagName").text());
    	$("#modalEditTagLabel").text("Edit a Playlist");
    	$("#edit-tag-color").val("#000000");
    	$("#labelTagNameEdit").text("Introduce the name of the new playlist:");
    	$("#edit-tag-isPlaylist").val("true");
    	$("#edit-tag-id").val($(this).parent().parent().attr("data-id"));
    });
});

