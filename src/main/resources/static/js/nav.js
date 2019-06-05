"use strict"
$(function() {
	
	  $("#notifications").on("click", notificationsHandler);
	
	
	
	$("#btnTogglePlayer").click(function() {
		let icon = $(this).find("i");
	    if($(icon).text() === "keyboard_arrow_right") {//hide
	    	$(icon).text("keyboard_arrow_left");
	    	$(this).css("margin-left", "-20px");
	    }
	    else {//show
	    	$(icon).text("keyboard_arrow_right");
	    	$(this).css("margin-left", "-8px");
	    }
	    
	    let sidebar = $("#btnTogglePlayer").parent();
	    
	    let divPlayer = $(sidebar.find("div")[0]);
	    if($(divPlayer).hasClass("d-none"))
	    	$(divPlayer).removeClass("d-none");
	    else
	    	$(divPlayer).addClass("d-none");
	    
	    if(sidebar.hasClass("col-3")) {
	    	sidebar.removeClass("col-3");
	    	sidebar.addClass("col-0");
	    }
	    else {
	    	sidebar.removeClass("col-0");
	    	sidebar.addClass("col-3");
	    }
	});
	
	if($("#msg").text() && $("#msg").text() != "") {
		$("#msgModal").modal("toggle");
	}
	
	$("#msg").change(function() {
		$("#msgModal").modal("toggle");
	});	
	
});





function notificationsHandler(){
	
	
	
	$.ajax({
		type:"GET",
		url: "/user/notifications",
		
		headers: {
			"Content-Type": "application/json",				
			"X-CSRF-TOKEN": m3.csrf.value
		},
		success : function(data){
			showNotifications(data);
			
			console.log("exito");
		},
		error: function (jqXHR, textStatus, errorThrown) {
			
            console.log("Se ha producido un error: " + errorThrown);
       }
	});
	
	
	
}


function showNotifications(listNotifications){
	
	
	
	listNotifications.forEach(notification =>{
		
		$("#notifications").append("<div style='color : white'>" + notification["text"] +"</div>");
	});
	
}
	
