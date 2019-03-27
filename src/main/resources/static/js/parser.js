"use strict"
var parser;
	
$(function() {
	
	parser = {
		legalAge: 18,
		emailPattern: /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
		namePattern: /^[\w\s]+$/,
		passwordPattern: /^(?=.{6,})(?=.*\d)(?=.*[A-Z]).*$/,
		nicknamePattern: /^[A-Za-z]+$/,
		
	    parseNotNullText : "Please, fill the field",
	    parseNotEmptyText : "Please, fill the field",
	    parseEmailText: "Please, introduce a valid email",
	    parseNameText: "Please, introduce only letters or numbers",
	    parsePasswordText: "Password must contains at least"+" 1 capital letter"+ ", 1 number"+" and 6 characters",
	    parseSamePasswordText: "Passwords must match",
	    parseIntegerText : "Please, introduce a number",
	    parseIntegerPositiveText: "Please, introduce a positive number",
	    parseDateText: "Please, introduce a valid date",
	    parseBirthdayText: "You must be at least 18 years old",

	    /**
	     * Resturn true if the argument is correct, if not return false
	     * @param {*} argument 
	     */
	    parseNotNull: function parseNotNull(argument) {
	        let result = (argument !== null && argument !== undefined);
	        return { ok: result, msg: (result ? null : parser.parseNotNullText) };
	    },
	    
	    parseStringNotEmpty: function parseStringNotEmpty(argument) {
	        argument = argument.trim();
	        let result = (argument !== "");
	        return { ok: result, msg: (result ? null : parser.parseNotNullText) };
	    },
	    
	    parseEmail: function parseEmail(argument) {
	    	let re = parser.emailPattern;
	    	let result = re.test(argument);
	    	return { ok: result, msg: (result ? null : parser.parseEmailText) };
	    },
	    
	    parseName: function parseName(argument) {
	    	let re = parser.namePattern;
	    	let result = re.test(argument);
	    	return { ok: result, msg: (result ? null : parser.parseNameText) };
	    },
	    
	    parsePassword: function parsePassword(argument) {
	    	let re = parser.passwordPattern;
	    	let result = re.test(argument);
	    	return { ok: result, msg: (result ? null : parser.parsePasswordText) };
	    },
	    
	    parseSamePassword: function parseSamePassword(argument, idPassword) {
	    	let password = $(idPassword).val();
	    	let result = (argument != null && password != null && argument === password);
	    	return { ok: result, msg: (result ? null : parser.parseSamePasswordText) };
	    },

	    parseInteger: function parseInteger(argument) {
	        let result = Number.isInteger(argument);
	        return { ok: result, msg: (result ? null : parser.parseIntegerText) };
	    },
	    
	    parseIntegerPostive: function parseIntegerPostive(argument) {
	    	let result = Number.isInteger(argument) && Number(argument) > 0;
	        return { ok: result, msg: (result ? null : parser.parseIntegerPositiveText) };
	    },
	    
	    parseDate: function parseDate(argument) {
	    	let result = new Date(argument);
	    	return { ok: result != null , msg: (result ? null : parser.parseDateText) };
	    },
	    
	    parseBirthday: function parseBirthday(argument) {
	    	let birthday = moment(new Date(argument));
	    	let legalDate = moment().subtract(parser.legalAge, 'year');
	    	
	    	if(birthday && legalDate) { 		
				let result = legalDate.isSame(birthday) || legalDate.isAfter(birthday);
				return { ok: result, msg: (result ? null : parser.parseBirthdayText) };
	    	}
	    	
	    	return { ok: birthday != null, msg: (result ? null : parser.parseDateText) };
	    },

	    cleanMessageError: function cleanMessageError(id) {
	    	$(id).removeClass("is-valid");
	        $(id).removeClass("is-invalid");
	        $(id).addClass("is-valid");
	        $(id).parent().find(".invalid-feedback").text("");
	    },

	    displayMessageError: function displayMessageError(id, msg) {
	    	$(id).removeClass("is-valid");
	        $(id).removeClass("is-invalid");
	        $(id).addClass("is-invalid");
	        $(id).parent().find(".invalid-feedback").text(msg);
	    },

	    parse: function parse(id, parseFunction, opc) {
	        let input = $(id);
	        let argument = input.val();
	        let msg = null;
	        let required = $(id).attr("required");

	        let notNull = parser.parseNotNull(argument);

	        if(notNull.ok) {
	            let result = parseFunction(argument, opc);
	            msg = result.msg;
	        }
	        else if(required) {
	            msg = notNull.msg;
	        }

	        let ok = (msg === null || msg === "");
	        
	        if(!ok) {
	            parser.displayMessageError(id, msg);
	        }
	        else {
	            parser.cleanMessageError(id);
	        }
	        
	        return ok;
	    }
				
	};
	
});
