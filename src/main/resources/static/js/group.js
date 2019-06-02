$(() => {
	$("#select-all-groups").on("change", selectAllGroupsCheckBoxHandler);
	$("#delete-groups").on("click", deleteGroupsButtonHandler);
	$('.btn-edit-group').on('click', editGroupModalHandler);
	$("#btn-add-members").on("click", addMembersButtonHandler);
	$("#chat-message-submit-button").on("click", chatMessageSubmitButtonHandler);
	$(".delete-user-from-group-button").on("click", deleteUserFromGroupButtonHandler);
	$(".delete-user-in-session-from-group-button").on("click", deleteUserInSessionFromGroupButtonHandler);
	$(".change-permission-button").on("click", changePermissionButtonHandler);
	$("#modal-btn-change-permission").on("click", changePermissionModalButtonHandler);
    $(".btn-view-members").on("click", showMembersHandler);
    $("#search-group-input").keyup(searchGroupInputChangeHandler);
});
 


function searchGroupInputChangeHandler(){
	
	let groupName = $("#search-group-input").val();
	
	
	$.ajax({
		type:"GET",
		url: "/groups/searchGroup/" + groupName,
		dataType: 'json',
		headers: {
			"Content-Type": "application/json",				
			"X-CSRF-TOKEN": m3.csrf.value
		},
		success : function(data){
			showGroupsSearched(data);
			
			console.log("exito");
		},
		error: function (jqXHR, textStatus, errorThrown) {
			
            console.log("Se ha producido un error: " + errorThrown);
       }
	});
	
}


function showGroupsSearched(listGroups){
	let elemento;
	
	$("#list-of-groups-ul").empty();       
	listGroups.forEach(group =>{
		elemento = "<li class='list-group-item'>" +
				"<div class='btn-group dropright'>" +
				"<input type ='checkbox' value='" + group.id + "' name='group-check' class='ml-1 mr-3 file-checkbox'>" +
						"<i class='material-icons mr-3'>group</i><a class='d-flex align-items-center' href='" + group.id + "'>" +
						" <span class='link link-group' href='' name='groupName' >" + group.name + "</span></a>" +
								"<button class='btn no-bck' data-toggle='dropdown' aria-haspopup='true' aria-expanded='false'><i class='material-icons color-icon'>more_vert</i></button>" +
								"<div class='dropdown-menu'>" +
								"<a class='dropdown-item btn-edit-group' data-group-id='" + group.id + "' data-group-name='" + group.name + "' href='#' data-toggle='modal' data-target='#modalEditGroup'><i class='material-icons mr-2'>edit</i>Edit group</a>" +
								"<a class='dropdown-item  btn-view-members' href='#'  data-group-id='" + group.id + "' data-toggle='modal' data-target='#modalShowMembers'><i class='material-icons mr-2'>remove_red_eye</i>View members</a>" +
								"<a class='dropdown-item' href='#'><i class='material-icons mr-2'>share</i>Share with group</a>" +
								"<a class='dropdown-item delete-user-in-session-from-group-button' href='#' th:attr='data-user-id=${session.u.id},  data-group-id='" + group.id + "'><i class='material-icons mr-2'>exit_to_app</i>Leave the group</a>" +
								"</div></div></li>";
          $("#list-of-groups-ul").append(elemento);              
		
	});
}


function showMembersHandler(){
	
	
	let groupId = $(this).attr('data-group-id');
	console.log(groupId);
	
	$.ajax({
		type:"GET",
		url: "/groups/viewMembers/" + groupId,
		dataType: 'json',
		headers: {
			"Content-Type": "application/json",				
			"X-CSRF-TOKEN": m3.csrf.value
		},
		success : function(data){
			showMembersInModal(data);
			
			console.log("exito");
		},
		error: function (jqXHR, textStatus, errorThrown) {
			
            console.log("Se ha producido un error: " + errorThrown);
       }
	});
	
}


function showMembersInModal(listOfMembers){
	
	let elemento;
	
	$("#members-list").empty();
	listOfMembers.forEach(member =>{
		
		elemento = "<li class=' d-flex flex-row'>" +
		"<label class='friendPicker'>" +
		"<span class='friendPicker'>" +
			"<img class='profile-image' src='" + member.avatar + "'>"+
			"<span class='d-flex flex-column align-items-center mr-3'>" +
				"<span><a class='link profile-name name' href='/user/profile?userId=" + member.id + "'>" + member.name + "</a></span>" +
				"<span class='nick text-center'>@" + member.nickname  + "</span>" +
		"</span></span></label></li>";
		$("#members-list").append(elemento);			 
		
	});
	
		
	
}


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

function deleteUserInSessionFromGroupButtonHandler() {
	let userId = $(this).attr("data-user-id");
	let groupId = $(this).attr("data-group-id");
	deleteMember(true, userId, groupId);
}

function deleteUserFromGroupButtonHandler() {
	let userId = $(this).attr("data-user-id");
	let groupId = $(this).attr("data-group-id");
	deleteMember(false, userId, groupId);
}

function deleteMember(isUserInSession, userId, groupId) {
	
	
	$.ajax({
		type:"POST",
		url: "/groups/deleteMember",
		data: JSON.stringify({userId : userId, groupId : groupId}),
		dataType: 'json',
		headers: {
			"Content-Type": "application/json",				
			"X-CSRF-TOKEN": m3.csrf.value
		},
		success : function(){
			if (isUserInSession) {
				window.location = "http://localhost:8080/groups/";
			} else {
				location.reload();
			}
			
			console.log("exito");
		},
		error: function (jqXHR, textStatus, errorThrown) {
			if (isUserInSession) {
				window.location = "http://localhost:8080/groups/";
			} else {
				location.reload();
			}
            console.log("Se ha producido un error: " + errorThrown);
       }
	});
}


function changePermissionButtonHandler() {
	let userId = $(this).attr("data-user-id");
	
	$("#modalChangePermission").find("#change-permission-user-id").val(userId);
}

function changePermissionModalButtonHandler() {
	let userId = $("#modalChangePermission").find("#change-permission-user-id").val();
	let permission = $("#permission-select").find(":selected").text();
	let groupId = $("#title-name-group").attr("data-group-id");
	
	$.ajax({
		type:"POST",
		url: "/groups/changePermission",
		data: JSON.stringify({userId : userId, permission : permission, groupId : groupId}),
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
