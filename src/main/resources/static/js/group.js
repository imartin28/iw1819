$(() => {
	$("#select-all-groups").on("change", selectAllGroupsCheckBoxHandler);
	$("#delete-groups").on("click", deleteGroupsButtonHandler);
	$('.btn-edit-group').on('click', editGroupModalHandler);
	$("#btn-add-members").on("click", addMembersButtonHandler);
	$("#chat-message-submit-button").on("click", chatMessageSubmitButtonHandler);
	$(".delete-user-from-group-button").on("click", deleteUserFromGroupButtonHandler);
});
 



function chatMessageSubmitButtonHandler(event) {
	event.preventDefault();
}

function addMembersButtonHandler(){
	
	let membersChecked = $("input[name='members-check']:checked");
	
	let array_IdsAddMembers = [];
	
	membersChecked.each(function(){
		let member = $(this);		
		array_IdsAddMembers.push(member.val());		
	});
	
	
	let idGroup = $("#title-name-group").attr("data-group-id");
	array_IdsAddMembers.push(idGroup);
	
	peticionAjaxConListaIds(array_IdsAddMembers, "/groups/addMembers");
}


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


function editGroupModalHandler() {

	let groupId = $(this).attr('data-group-id');
	let groupName = $(this).attr('data-group-name');
	
	
	$("#modalEditGroup").find("#edit-group-id").val(groupId);
	$("#modalEditGroup").find("#edit-group-name").val(groupName);
	
}

function deleteGroupsButtonHandler() {
	let groupsChecked = $("input[name='group-check']:checked");
	
	let array_IdsToDelete = [];
	
	groupsChecked.each(function(){
		let group = $(this);		
		array_IdsToDelete.push(group.val());		
	});

	peticionAjaxConListaIds(array_IdsToDelete, "/groups/deleteGroups");
}



function peticionAjaxConListaIds(arrayIds, url) {
	$.ajax({
		type:"POST",
		url: url,
		data: JSON.stringify(arrayIds),
		dataType: 'json',
		headers: {
			"Content-Type": "application/json",				
			"X-CSRF-TOKEN": m3.csrf.value
		},
		success : function(){
			location.reload();
			console.log("exito");
		},
		error: function (jqXHR, textStatus, errorThrown) {
			 location.reload();
            console.log("Se ha producido un error: " + errorThrown);
       }
	});
}

function deleteUserFromGroupButtonHandler() {
	let userId = $(this).attr("data-user-id");
	let groupId = $("#title-name-group").attr("data-group-id");
	
	$.ajax({
		type:"POST",
		url: "/groups/deleteMember",
		data: {userId : userId, groupId : groupId},
		dataType: 'json',
		headers: {
			"Content-Type": "application/json",				
			"X-CSRF-TOKEN": m3.csrf.value
		},
		success : function(){
			
			console.log("exito");
		},
		error: function (jqXHR, textStatus, errorThrown) {
            console.log("Se ha producido un error: " + errorThrown);
       }
	});
}


