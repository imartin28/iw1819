
var correctNewTagForm = [];

$(() => { 
	$("#tagSearchForm").submit(function() { return false });
	$("#tagSearchInput").keyup(searchTag);
    $("#new-tag-name, #edit-tag-name").keyup(handleValidateTagName);
    $("#new-tag-form").submit(handleValidateTagName);
    $('.btn-edit-tag').on('click', handleEditModal);
    $("#add-tags-to-file-button").on("click", addTagsToFileButtonHandler);
    $(".remove-tag-from-file-button").on("click", removeTagFromFileButtonHandler);
});


function submitForm() {
	return handleValidateTagName();
};

function searchTag() {
	let linkTagNames = $("#tagList").find(".tagName");
	let searchText = $("#tagSearchInput").val();
	
	if(searchText.length > 0 && searchText !== "") {
		
		searchText = searchText.toLowerCase().trim();
		let tags = linkTagNames.filter(function (index) {
			let tagName = $(linkTagNames[index]).text();
			tagName = tagName.toLowerCase().trim();
			return tagName === searchText || tagName.includes(searchText);
		});
		
		if(tags !== null && tags.length > 0) {
			let tagsLi = $("#tagList").find("li");
			
			if(tagsLi !== null && tagsLi.length > 0) {
				hideAllTagsLi();
				
				for(let i = 0; i < tags.length; i++) {
					showTagLi($(tags[i]).parent().parent());
				}
			}
		}
		else {
			//hideAll
			hideAllTagsLi();
		}
	}
	
	if(searchText === "") {
		showAllTagsLi();
	}
}

function hideTagLi(li) {
	if($(li).hasClass("d-flex")) {
		$(li).removeClass("d-flex");
	}
	if(!$(li).hasClass("d-none")) {
		$(li).addClass("d-none");
	}
}

function hideAllTagsLi() {
	let tagsLi = $("#tagList").find("li");
	
	if(tagsLi !== null && tagsLi.length > 0) {
		for(let i = 0; i < tagsLi.length; i++) {
			hideTagLi(tagsLi[i]);
		}
	}
}

function showTagLi(li) {
	if(!$(li).hasClass("d-flex")) {
		$(li).addClass("d-flex");
	}
	if($(li).hasClass("d-none")) {
		$(li).removeClass("d-none");
	}
}

function showAllTagsLi() {
	let tagsLi = $("#tagList").find("li");
	
	if(tagsLi !== null && tagsLi.length > 0) {
		for(let i = 0; i < tagsLi.length; i++) {
			showTagLi(tagsLi[i]);
		}
	}
}

function handleValidateTagName(event) {
	let target = $(event.target);
	let id = "#" + target.attr('id');
	let ok = parser.parse(id, parser.parseName && parser.parseTagName, validateTagName(event));
	
	target.parent().parent().find("#btn-create-tag-form, #btn-edit-tag-form").attr('disabled', !ok);
	return ok;
}


function validateTagName(event) {
	let target = $(event.target);
	let id = "#" + target.attr('id');
	let tagName = $(id).val();
	let result = null;
	let tagId = null;
	
	if (id == '#edit-tag-name') {
		tagId = $("#edit-tag-id").val();
	}
	
	
	
	$.ajax({
		type:"GET",
		url:"/file/validateTagName",
		async : false,
		headers: {
			"Content-Type": "application/json",				
			"X-CSRF-TOKEN": m3.csrf.value
		},
		data : {name : tagName, tagId : tagId},
		
		success: function (data, textStatus, jqXHR) {
			console.log(data + " exito");
			result = data;
		},
       
        error: function (jqXHR, textStatus, errorThrown) {
             console.log("Se ha producido un error: " + errorThrown);
        }
		
	});
	
	return result;
}


function handleEditModal() {
	let tagId = $(this).attr('data-tag-id');
	let tagName = $(this).attr('data-tag-name');
	let tagColor = $(this).attr('data-tag-color');
	
	$("#modalEditTag").find("#edit-tag-id").val(tagId);
	$("#modalEditTag").find("#edit-tag-name").val(tagName);
	$("#modalEditTag").find("#edit-tag-color").val(tagColor);
}


function addTagsToFileButtonHandler() {
	let tagsChecked = $("input[name='id_tag']:checked");
	let tagsIds = [];
	let fileId = $(this).parent().parent().find("input[type='hidden']").val();

	tagsChecked.each(function(){
		let tag = $(this);		
		tagsIds.push(tag.val());		
	});
	
	addTagsToFile(tagsIds, fileId);
	
}


function addTagsToFile(tagsIds, fileId) {
	$.ajax({
		type:"POST",
		url:"/file/addTagsToFile",
		data: JSON.stringify({tagsIds : tagsIds, fileId : fileId}),
		contentType : 'application/json; charset=utf-8',
        dataType : 'json',
		headers: {
			"Content-Type": "application/json",				
			"X-CSRF-TOKEN": m3.csrf.value
		},
		success : function(){
			location.reload();
			console.log("exito");
		},
		 error: function (jqXHR, textStatus, errorThrown) {
			 location.reload();
             console.log("Se ha producido un error: " + errorThrown);
        }
	});
}


function removeTagFromFileButtonHandler() {
	let fileId = $(this).attr("data-file-id");
	let tagId = $(this).attr("data-tag-id");
	
	$.ajax({
		type:"POST",
		url:"/file/removeTagFromFile",
		data: JSON.stringify({tagId : tagId, fileId : fileId}),
		contentType : 'application/json; charset=utf-8',
        dataType : 'json',
		headers: {
			"Content-Type": "application/json",				
			"X-CSRF-TOKEN": m3.csrf.value
		},
		success : function(){
			location.reload();
			console.log("exito");
		},
		 error: function (jqXHR, textStatus, errorThrown) {
			 location.reload();
             console.log("Se ha producido un error: " + errorThrown);
        }
	});
}


function dragStart(event) {
	event.dataTransfer.setData("tagId", event.target.getAttribute("data-tag-id"));
}


function allowDrop(event) {
	event.preventDefault();
}

function drop(event) {
  event.preventDefault();
  let tagId = event.dataTransfer.getData("tagId");
  let fileId = event.target.getAttribute("data-file-id");
  
  addTagsToFile([tagId], fileId);
}


