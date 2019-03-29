$(() => {

   $("#delete-files").on("click", deleteFilesButtonHandler);
    
    
    $("#button-upload-file").on("click", uploadFileButtonHandler);

});



function deleteFilesButtonHandler(){
	let filesChecked = $("input[name='file-check']:checked");
	
	let array_IdsToDelete = [];
	
	filesChecked.each(function(){
		let file = $(this);		
		array_IdsToDelete.push(file.val());		
	});

	
	
	deleteFiles(array_IdsToDelete);
	
}


function deleteFiles(array_IdsToDelete){


	
	$.ajax({
		type:"POST",
		url:"/file/deleteFiles",
		data: JSON.stringify(array_IdsToDelete),
		dataType: 'json',
		headers: {
			"Content-Type": "application/json",				
			"X-CSRF-TOKEN": m3.csrf.value
		}
		
	});
	
}