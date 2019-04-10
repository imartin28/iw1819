
var correctNewTagForm = [];

$(() => { 
    $("#new-tag-name, #edit-tag-name").keyup(handleValidateTagName);
    $("#new-tag-form").submit(handleValidateTagName);
    $('.btn-edit-tag').on('click', handleEditModal);
});


function submitForm() {
	return handleValidateTagName();
};


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





