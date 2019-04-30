$(() => {
	$("#select-all-groups").on("change", selectAllGroupsCheckBoxHandler);
	$("#delete-groups").on("click", deleteGroupsButtonHandler);
});



function selectAllGroupsCheckBoxHandler() {
	
	let inputs = $("input[name='group-check']");
	
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

function deleteGroupsButtonHandler() {
	let groupsChecked = $("input[name='group-check']:checked");
	
	let array_IdsToDelete = [];
	
	groupsChecked.each(function(){
		let group = $(this);		
		array_IdsToDelete.push(group.val());		
	});

	deleteGroups(array_IdsToDelete);
}



function deleteGroups(array_IdsToDelete){
	$.ajax({
		type:"POST",
		url:"/groups/deleteGroups",
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


