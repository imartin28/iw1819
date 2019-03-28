"use strict"
$(function() {
	
	//
	// $('#element').donetyping(callback[, timeout=1000])
	// Fires callback when a user has finished typing. This is determined by the time elapsed
	// since the last keystroke and timeout parameter or the blur event--whichever comes first.
	//   @callback: function to be called when even triggers
	//   @timeout:  (default=1000) timeout, in ms, to to wait before triggering event if not
	//	              caused by blur.
	// Requires jQuery 1.7+
	//
    $.fn.extend({
        donetyping: function(callback,timeout){
            timeout = timeout || 1.5e3; // 1 second default timeout
            var timeoutReference,
                doneTyping = function(el){
                    if (!timeoutReference) return;
                    timeoutReference = null;
                    callback.call(el);
                };
            return this.each(function(i,el){
                var $el = $(el);
                // Chrome Fix (Use keyup over keypress to detect backspace)
                // thank you @palerdot
                $el.is(':input') && $el.on('keyup keypress paste',function(e){
                    // This catches the backspace button in chrome, but also prevents
                    // the event from triggering too preemptively. Without this line,
                    // using tab/shift+tab will make the focused element fire the callback.
                    if (e.type=='keyup' && e.keyCode!=8) return;
                    
                    // Check if timeout has been set. If it has, "reset" the clock and
                    // start over again.
                    if (timeoutReference) clearTimeout(timeoutReference);
                    timeoutReference = setTimeout(function(){
                        // if we made it here, our timeout has elapsed. Fire the
                        // callback
                        doneTyping(el);
                    }, timeout);
                }).on('blur',function(){
                    // If we can, fire the event since we're leaving the field
                    doneTyping(el);
                });
            });
        }
    });

    /* ModifyUser form */
    var correctUserForm = [];
    
    function handleEmail(){
    	correctUserForm[1] = parser.parse('#email', parser.parseEmail);
	}
    $('#email').donetyping(handleEmail);
    $('#email').change(handleEmail);
	
    function handleName(){
		correctUserForm[2] = parser.parse('#name', parser.parseName);
	}
	$('#name').donetyping(handleName);
	$('#name').change(handleName);
	
	function handleLastName(){
		correctUserForm[3] = parser.parse('#lastName', parser.parseName);
	}
	$('#lastName').donetyping(handleLastName);
	$('#lastName').change(handleLastName);
	
	function handleBirthdate(){
		correctUserForm[4] = parser.parse('#birthdate', parser.parseBirthday);
	}
	$('#birthdate').donetyping(handleBirthdate);
	$('#birthdate').change(handleBirthdate);
    
    $("#modifyProfileForm").submit(function() {
    	let allCorrect = true;
    	
    	for (let i = 0; i < correctUserForm.length; i++) {
    		allCorrect = allCorrect && correctUserForm[i];
    	}
    	
		return allCorrect;
    });
    
    
    /* ModifyPassword form */
	var correctPasswordForm = [];
	
	function handleOldPassword(){
		correctPasswordForm[1] = parser.parse('#oldPassword', parser.parsePassword);
	}
	$('#oldPassword').donetyping(handleOldPassword);
	$('#oldPassword').change(handleOldPassword);
	
	function handleNewPassword(){
		correctPasswordForm[2] = parser.parse('#newPassword', parser.parsePassword);
	}
	$('#newPassword').donetyping(handleNewPassword);
	$('#newPassword').change(handleNewPassword);
	
    $("#modifyPasswordForm").submit(function() {
    	let allCorrect = true;
    	
    	for (let i = 0; i < correctPasswordForm.length; i++) {
    		allCorrect = allCorrect && correctPasswordForm[i];
    	}
    	
		return allCorrect;
    });
	
});