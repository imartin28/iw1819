"use strict"
$(function () {
	
	initMetadata();
	
});

function initMetadata() {
	let table = $('#metadata');
	for (let key in metadata) {
		let title = $('<div class="title"></div>');
		title.text(key);
		table.append(title);
		let description = $('<div class="description"></div>');
		description.text(metadata[key]);
		table.append(description);
	}
};