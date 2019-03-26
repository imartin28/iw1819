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

    /* Person form */
    var correctUserForm = [];
    
    $('#email').donetyping(function(){
    	correctUserForm["email"] = parser.parse('#email', parser.parseEmail);
	});
	
	$('#name').donetyping(function(){
		correctUserForm["name"] = parser.parse('#name', parser.parseName);
	});
	
	$('#lastName').donetyping(function(){
		correctUserForm["lastName"] = parser.parse('#lastName', parser.parseName);
	});
	
	$('#birthdate').donetyping(function(){
		correctUserForm["birthdate"] = parser.parse('#birthdate', parser.parseBirthday);
	});
	
	/*
	$('#OldPassword').donetyping(function(){
		correctUserForm["OldPassword"] = parser.parse('#OldPassword', parser.parsePassword);
	});
	
	$('#NewPassword').donetyping(function(){
		correctUserForm["NewPassword"] = parser.parse('#NewPassword', parser.parseSamePassword, '#OldPassword');
	});
	*/
    
    $("#modifyProfileForm").submit(function() {
    	let allCorrect = true;
    	
    	for (let correctInput in correctUserForm) {
    		allCorrect = allCorrect && correctInput;
    	}
    	
		return allCorrect;
    });
    
});