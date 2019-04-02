$(() => {
   $(".custom-file-input").on("change", appearFileName);	 
   $("#delete-files").on("click", deleteFilesButtonHandler);
   //$(".file-upload").file_upload();
   $("#select-all-files").on("change", selectAllFilesCheckBoxHandler);
});



function selectAllFilesCheckBoxHandler(){
	
	if( $(this).is(':checked') ){
		$("input[name='file-check']").prop("checked", true);
    } else {
    	$("input[name='file-check']").prop("checked", false);
    }
}



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
		},
		success : function(){
			location.reload();
			console.log("exito");
		},
		error : function(){
			console.log("error");
		}
		
	});
	
}

/*function mouseEnterOnFileHandler(event) {
	$(this).children().eq(0).removeClass("visibility-hidden");
}

function mouseLeaveFileHandler(event) {
	
	$(this).children().eq(0).addClass("visibility-hidden");
}*/
