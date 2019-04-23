"use strict"
$(function() {

	var metadata = {};
	var count = 0;
	
	$('#addMetadata').on("click", function() {
		addMetadata();
		let elem = $('#editMetadata');
		elem.append('<input type="text" class="keyMetadata-' + count + '">');
		elem.append('<input type="text" class="valueMetadata-' + count + '">');
	});
	
	$('#submitMetadata').on("click", function() {
		// Pendiente realizar post de los metadatos
	});
	
	initMetadata();
	
});

function initMetadata() {
	// Pendiente inicializar tabla con los metadatos ya incluidos en el archivo
}

function addMetadata() {
	let key = $('#keyMetadata-' + count).text();
	let value = $('#valueMetadata-' + count).text();
	metadata[key] = value;
	count++;
}