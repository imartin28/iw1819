var count = 0;

"use strict"
$(function() {
	
	$('#addMetadata').on("click", function() {
		if (addMetadata()) {
			let elem = $('#editMetadata');
			elem.append('<input type="text" id="keyMetadata-' + count + '">');
			elem.append('<input type="text" id="valueMetadata-' + count + '">');
		}
	});
	
	$('#submitMetadata').on("click", function() {
		// Pendiente realizar post de los metadatos
	});
	
});

function addMetadata() {
	let key = $('#keyMetadata-' + count).val();
	let value = $('#valueMetadata-' + count).val();
	if (key != "" && value != "") {
		metadata[key] = value;
		count++;
		return true;
	}
	else return false;
}
