$(() => {
	$("#fileSearchForm").submit(function() { return false });
	$("#fileSearchInput").keyup(searchFile);
	$(".custom-file-input").on("change", appearFileName);	 
	$("#delete-files").on("click", deleteFilesButtonHandler);
	$("#download-files").on("click", downloadFilesButtonHandler);
	//$(".file-upload").file_upload();
	$("#select-all-files").on("change", selectAllFilesCheckBoxHandler);
	$("#button-upload-file").on("click", uploadFileButtonHandler);
	changeFilesIcons();
  
});

function searchFile() {
	let filesPanel = $(".panel-grid-files").find(".file-name > a");
	let searchText = $("#fileSearchInput").val();
	
	if(searchText.length > 0 && searchText !== "") {
		
		searchText = searchText.toLowerCase().trim();
		let files = filesPanel.filter(function (index) {
			let fileName = $(filesPanel[index]).text();
			fileName = fileName.toLowerCase().trim();
			return fileName === searchText || fileName.includes(searchText);
		});
		
		if(files !== null && files.length > 0) {
			
			if(filesPanel !== null && filesPanel.length > 0) {
				hideAllFiles();
				
				for(let i = 0; i < files.length; i++) {
					showFile(files[i]);
				}
			}
		}
		else {
			hideAllFiles();
		}
	}
	
	if(searchText === "") {
		showAllFiles();
	}
}

function hideAllFiles() {
	let files = $(".panel-grid-files").find(".file-name > a");
	
	if(files !== null && files.length > 0) {
		for(let i = 0; i < files.length; i++) {
			hideFile(files[i]);
		}
	}
}

function hideFile(fileNameLink) {
	$(fileNameLink).parent().parent().parent().hide();
	$(fileNameLink).parent().parent().find("input:checkbox").prop("disabled", true);
	$(fileNameLink).parent().parent().find("input:checkbox").prop("checked", false);
}

function showAllFiles() {
	let files = $(".panel-grid-files").find(".file-name > a");
	
	if(files !== null && files.length > 0) {
		for(let i = 0; i < files.length; i++) {
			showFile(files[i]);
		}
	}
}

function showFile(fileNameLink) {
	$(fileNameLink).parent().parent().parent().show();
	$(fileNameLink).parent().parent().find("input:checkbox").prop("disabled", false);
}


function changeFilesIcons(){
	$(".icon-type").each(function(){
		
		let file = $(this);
		let mimetype = file.attr("data-mimetype");
		
		
		mimetype.split("/")[0];
		console.log("tipo " + mimetype.split("/")[0]);
		
		
	});
	
}


function selectAllFilesCheckBoxHandler(){
	
	let inputs = $("input[name='file-check']");
	
	if(inputs !== null && inputs.length > 0) {
		for(let i = 0; i < inputs.length; i++) {
			if(!$(inputs[i]).prop("disabled")) {
				if( $(this).is(':checked') ){
					$(inputs[i]).prop("checked", true);
			    } else {
			    	$(inputs[i]).prop("checked", false);
			    }
			}
		}
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

function uploadFileButtonHandler(event) {
	event.preventDefault();
	
	let file = document.getElementById("customFile").files[0];
	let reader = new FileReader();

	
	reader.onload = function(event) {
		var binary = event.target.result;
		let sha256 = CryptoJS.SHA256(binary).toString();
		uploadFile(sha256, file);
	};
	
	reader.onerror = function() {
        console.error("Could not read the file");
    };
	
    
    reader.readAsBinaryString(file);
}

function uploadFile(sha256, file) {
	let formdata = new FormData();
	let currentTagId = $("input[name='currentTagId']").val();
	
	formdata.append("file", file);
	formdata.append("sha256", sha256);
	formdata.append("currentTagId", currentTagId);
	console.log("sha256 : " + formdata.get("sha256"));
	console.log(formdata.get("file"));
	
	$.ajax({
		type:"POST",
		url:"/file/upload",
		data: formdata,
		contentType : false,
		processData : false,
		cache : false,
		enctype: 'multipart/form-data',
		headers: {		
			"X-CSRF-TOKEN": m3.csrf.value
		},
		success : function(){
			location.reload();
			console.log("exito");
		},
		error : function(xhr, ajaxOptions, thrownError){
			console.log(thrownError);
			console.log(xhr);
		}
		
	});
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
		url:"/file/zip",
		data: JSON.stringify(array_IdsToDownload),
		headers: {			
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

