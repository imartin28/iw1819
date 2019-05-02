var count = 0;

"use strict"
$(function() {
	
	initMetadata();
	
	$('#addMetadata').on("click", function() {
		if (addMetadata()) {
			let button = $('<input type="checkbox" id="buttonMetadata-' + count + '">');
			button.css("visibility", "hidden");
			$('#editMetadata').append(button);
			$('#editMetadata').append('<input type="text" id="keyMetadata-' + count + '">');
			$('#editMetadata').append('<input type="text" id="valueMetadata-' + count + '">');
		}
	});
	
	$('#removeMetadata').on("click", function() {
		for (let i = 0; i < count; i++)
			removeMetadata(i);
	});
	
});

function initMetadata() {
	for (let key in metadata) {
		$('#editMetadata').append($('<input type="checkbox" id="buttonMetadata-' + count + '">'));
		let keyElem = $('<input type="text" id="keyMetadata-' + count + '">');
		keyElem.val(key);
		$('#editMetadata').append(keyElem);
		let valueElem = $('<input type="text" id="valueMetadata-' + count + '">');
		valueElem.val(metadata[key]);
		$('#editMetadata').append(valueElem);
		count++;
		keyElem.prop('disabled', true);
		valueElem.prop('disabled', true);
	}
	let button = $('<input type="checkbox" id="buttonMetadata-' + count + '">');
	button.css("visibility", "hidden");
	$('#editMetadata').append(button);
	$('#editMetadata').append($('<input type="text" id="keyMetadata-' + count + '">'));
	$('#editMetadata').append($('<input type="text" id="valueMetadata-' + count + '">'));
	$('#newMetadata').val(JSON.stringify(metadata));
};

function addMetadata() {
	let button = $('#buttonMetadata-' + count);
	let key = $('#keyMetadata-' + count);
	let value = $('#valueMetadata-' + count);
	if (key.val() != "" && value.val() != "") {
		metadata[key.val()] = value.val();
		button.css("visibility", "visible");
		key.prop('disabled', true);
		value.prop('disabled', true);
		$('#newMetadata').val(JSON.stringify(metadata));
		count++;
		return true;
	}
	else return false;
};

function removeMetadata(index) {
	let button = $('#buttonMetadata-' + index);
	let key = $('#keyMetadata-' + index);
	let value = $('#valueMetadata-' + index);
	if (button.prop('checked')) {
		delete metadata[key.val()];
		button.remove();
		key.remove();
		value.remove();
		$('#newMetadata').val(JSON.stringify(metadata));
	}
};
