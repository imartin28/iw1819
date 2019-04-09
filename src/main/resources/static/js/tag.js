
var correctNewTagForm = [];

$(() => { 
    $('#new-tag-name').keyup(handleValidateTagName);
    $("#new-tag-form").submit(handleValidateTagName); 
});



function submitForm() {
	return handleValidateTagName();
};

function handleValidateTagName() {
	
	let ok = parser.parse('#new-tag-name', parser.parseName && parser.parseTagName, validateTagName());
	$("#btn-create-form").attr('disabled', !ok);
	return ok;
}

function validateTagName() {
	let tagName = $('#new-tag-name').val();
	let result = null;
	
	$.ajax({
		type:"GET",
		url:"/file/validateTagName",
		async : false,
		headers: {
			"Content-Type": "application/json",				
			"X-CSRF-TOKEN": m3.csrf.value
		},
		data : {name : tagName},
		
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
