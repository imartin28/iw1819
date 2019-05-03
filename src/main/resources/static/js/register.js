"use strict"
$(function() {
	
	var cssBold = {'font-weight' : 'bold', 'color' : '#ffffff', 'border-width' : '5px'};
	var cssBoldReset = {'font-weight' : '', 'color' : '', 'border-width' : ''};
	$(".log-in").css(cssBold);
	$(".log-in").click(function() {
		$(this).css(cssBold);
		$(".sign-up").css(cssBoldReset);
	});
	$(".sign-up").click(function() {
		$(this).css(cssBold);
		$(".log-in").css(cssBoldReset);
	});
	
	$(".login-tab").on("click", () => {
		$("#login-tab-content").addClass("active");
		$("#signup-tab-content").removeClass("active");
	});
	
	$(".sign-up").on("click", () => {
		$("#signup-tab-content").addClass("active");
		$("#login-tab-content").removeClass("active");
	});
	
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

    
    
    /* Login form */
    var correctLoginForm = [];
    
    function handleLoginUser() {
    	correctLoginForm[0] = (parser.parse('#username', parser.parseEmail) || parser.parse('#username', parser.parseNickname));
    }
    $('#username').donetyping(handleLoginUser);
    $('#username').change(handleLoginUser);
    
    /*
	function handleLoginPassword() {
		correctLoginForm[1] = parser.parse('#password', parser.parsePassword);
	}
	$('#password').donetyping(handleLoginPassword);
	$('#password').change(handleLoginPassword);
    */
    $("#login-form").submit(function() {
    	let allCorrect = true;
    	
    	for (let i = 0; i < correctLoginForm.length; i++) {
    		allCorrect = allCorrect && correctLoginForm[i];
    	}
    	
		return allCorrect;
    });
    
    
    
    /* Register form */
    var correctRegisterForm = [];
    
    function handleEmail() {
    	correctRegisterForm[0] = parser.parse('#inputEmail', parser.parseEmail);
    }
    $('#inputEmail').donetyping(handleEmail);
    $('#inputEmail').change(handleEmail);
	
    function handleNickname() {
    	correctRegisterForm[1] = parser.parse('#inputNickname', parser.parseNickname);
    }
	$('#inputNickname').donetyping(handleNickname);
	$('#inputNickname').change(handleNickname);
	
	function handlePassword() {
		correctRegisterForm[2] = parser.parse('#inputPassword', parser.parsePassword);
	}
	$('#inputPassword').donetyping(handlePassword);
	$('#inputPassword').change(handlePassword);
	
	function handleSamePassword() {
		correctRegisterForm[3] = parser.parse('#inputSamePassword', parser.parseSamePassword, '#inputPassword');
	}
	$('#inputSamePassword').donetyping(handleSamePassword);
	$('#inputSamePassword').change(handleSamePassword);
	
    $("#signup-form").submit(function() {
    	let allCorrect = true;
    	
    	for (let i = 0; i < correctRegisterForm.length; i++) {
    		allCorrect = allCorrect && correctRegisterForm[i];
    	}
    	
		return allCorrect;
    });
    
});