"use strict"
$(function () {
	
	initMetadata();
	
});

function initMetadata() {
	for (let key in metadata) {
		let title = $('<div class="title"></div>');
		title.text(key);
		$('#currentMetadata').append(title);
		let description = $('<div class="description"></div>');
		description.text(metadata[key]);
		$('#currentMetadata').append(description);
	}
};