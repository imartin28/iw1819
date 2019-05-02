var count = 0;

"use strict"
$(function() {
	
	initMetadata();
	
	$('#addMetadata').on("click", function() {
		if (addMetadata()) {
			let elem = $('#editMetadata');
			elem.append('<input type="text" id="keyMetadata-' + count + '">');
			elem.append('<input type="text" id="valueMetadata-' + count + '">');
		}
	});
	
});

function initMetadata() {
	for (let key in metadata) {
		let keyElem = $('<input type="text" id="keyMetadata-' + count + '">');
		keyElem.val(key);
		$('#editMetadata').append(keyElem);
		let valueElem = $('<input type="text" id="valueMetadata-' + count + '">');
		valueElem.val(metadata[key]);
		$('#editMetadata').append(valueElem);
		count++;
	}
	$('#editMetadata').append($('<input type="text" id="keyMetadata-' + count + '">'));
	$('#editMetadata').append($('<input type="text" id="valueMetadata-' + count + '">'));
	$('#newMetadata').val(JSON.stringify(metadata));
};

function addMetadata() {
	let key = $('#keyMetadata-' + count);
	let value = $('#valueMetadata-' + count);
	if (key.val() != "" && value.val() != "") {
		metadata[key.val()] = value.val();
		$('#newMetadata').val(JSON.stringify(metadata));
		count++;
		return true;
	}
	else return false;
}
