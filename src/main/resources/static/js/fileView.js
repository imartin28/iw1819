"use strict"
$(function () {
	
	initMetadata();
	
});

function initMetadata() {
	for (let key in metadata) {
		let title = $('<div class="title"></div>');
		title.text(key);
		$('#metadata').append(title);
		let description = $('<div class="description"></div>');
		description.text(metadata[key]);
		$('#metadata').append(description);
	}
};