$(() => {
   $(".custom-file-input").on("change", appearFileName);	 
   $("#delete-files").on("click", deleteFilesButtonHandler);
   $("#download-files").on("click", downloadFilesButtonHandler);
   //$(".file-upload").file_upload();
   $("#select-all-files").on("change", selectAllFilesCheckBoxHandler);
   changeFilesIcons();
  
});


function changeFilesIcons(){
	$(".icon-type").each(function(){
		
		let file = $(this);
		let mimetype = file.attr("data-mimetype");
		
		
		mimetype.split("/")[0];
		console.log("tipo " + mimetype.split("/")[0]);
		
		
	});
	
}


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


function downloadFilesButtonHandler(){
	let filesChecked = $("input[name='file-check']:checked");
	
	let array_IdsToDownload = [];
	
	filesChecked.each(function(){
		let file = $(this);		
		array_IdsToDownload.push(file.val());		
	});

	downloadFiles(array_IdsToDownload);
}


function downloadFiles(array_IdsToDownload){

	$.ajax({
		type:"POST",
		url:"",
		data: JSON.stringify(array_IdsToDownload),
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
