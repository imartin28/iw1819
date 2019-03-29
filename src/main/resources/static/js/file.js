$(() => {
	
	 
	
   $(".custom-file-input").on("change", appearFileName);	 
   $("#delete-files").on("click", deleteFilesButtonHandler);
   $(".panel-grid-files-element").mouseenter(mouseEnterOnFileHandler); 
   $(".panel-grid-files-element").mouseleave(mouseLeaveFileHandler);
   $("#button-upload-file").on("click", uploadFileButtonHandler);
});




function appearFileName(){
	 
  let fileName = $(this).val().split("\\").pop();
  $(this).siblings(".custom-file-label").addClass("selected").html(fileName);
}

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

function mouseEnterOnFileHandler(event) {
	$(this).children().eq(0).removeClass("visibility-hidden");
}

function mouseLeaveFileHandler(event) {
	
	$(this).children().eq(0).addClass("visibility-hidden");
}
