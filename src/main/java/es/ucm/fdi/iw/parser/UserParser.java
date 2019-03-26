package es.ucm.fdi.iw.parser;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.servlet.ModelAndView;

import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.transfer.UserTransfer;

public class UserParser extends Parser {
	
	private static UserParser instance;
	
	public static UserParser getInstance() {
		if(instance == null)
			instance = new UserParser();
		return instance;
	}

    private static final String EMAIL_PATTERN = "^[^@]+@[^@]+\\.[a-zA-Z]{2,}$";
    private static final String NAME_PATTERN = "^[a-zA-ZáéíóúñÁÉÍÓÚÑ- ]*$";
    private static final String EMAIL_EXAMPLE = "ejemplo@ejemplo.es";
    private static final String PASSWORD_PATTERN = "^(?=.{6,})(?=.*\\d)(?=.*[A-Z]).*$";
    private static final int EMAIL_MIN_LENGTH = 5;
    private static final int PASSWORD_MIN_LENGTH = 6;
    private static final int BIRTHDAY_MIN_AGE = 18;

    private static final int USER_ERROR_CODE = 3000;
    
    //Email
    private static final int USER_EMAIL_ERROR_CODE = 100;
    private static final int PARSE_COD_EMAIL_PATTERN = USER_ERROR_CODE+USER_EMAIL_ERROR_CODE+1;
    private static final String PE_MSG_EMAIL_PATTERN = "Email inválido, debe ser de la forma: "+EMAIL_EXAMPLE;

    //Name
    private static final int USER_NAME_ERROR_CODE = 200;
    private static final int PARSE_COD_NAME_PATTERN = USER_ERROR_CODE+USER_NAME_ERROR_CODE+1;
    private static final String PE_MSG_NAME_PATTERN = "Nombre inválido, solo puede contener letras";
    
    //Passwords
    private static final int USER_PASSWORD_ERROR_CODE = 300;
    
    private static final int PARSE_COD_PASSWORD_LENGTH = USER_ERROR_CODE+USER_PASSWORD_ERROR_CODE+1;
    private static final String PE_MSG_PASSWORD_LENGTH = "La contraseña debe contener al menos "+PASSWORD_MIN_LENGTH+" caracteres";

    private static final int PARSE_COD_PASSWORD_PATTERN = USER_ERROR_CODE+USER_PASSWORD_ERROR_CODE+2;
    private static final String PE_MSG_PASSWORD_PATTERN = "La contraseña debe contener al menos "+PASSWORD_MIN_LENGTH+" caracteres";
    
    private static final int PARSE_COD_PASSWORDS_DINDT_MATCH = USER_ERROR_CODE+USER_PASSWORD_ERROR_CODE+3;
    private static final String PE_MSG_PASSWORDS_DINDT_MATCH = "La contraseñas no coinciden";

    //Birthday
    private static final int USER_BIRTHDAY_ERROR_CODE = 400;
    private static final int PARSE_COD_BIRTHDAY_MENOR_EDAD = USER_ERROR_CODE+USER_BIRTHDAY_ERROR_CODE+1;
    private static final String PE_MSG_BIRTHDAY_MENOR_EDAD = "Debes ser mayor de "+BIRTHDAY_MIN_AGE+" años";


    // ---- CHECKS ---- //

    public UserParser() {}

    //Email
    public static boolean isValidEmail(String email) throws ParseException {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches())//"x@x.x"
            throw new ParseException(PE_MSG_EMAIL_PATTERN, PARSE_COD_EMAIL_PATTERN);

        return true;
    }

    public static boolean isValidEmailLength(String email) throws ParseException {
        if(email.length() < EMAIL_MIN_LENGTH)
            throw new ParseException(PE_MSG_EMAIL_PATTERN, PARSE_COD_EMAIL_PATTERN);

        return true;
    }

    //Nombre
    public static boolean isValidName(String nombre) throws ParseException {
        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher matcher = pattern.matcher(nombre);
        if (!matcher.matches())
            throw new ParseException(PE_MSG_NAME_PATTERN, PARSE_COD_NAME_PATTERN);

        return true;
    }

    //Passwords
    public static boolean isValidPassword(String password) throws ParseException {
        if (password.length() < PASSWORD_MIN_LENGTH)
            throw new ParseException(PE_MSG_PASSWORD_LENGTH, PARSE_COD_PASSWORD_LENGTH);
        
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        if (!matcher.matches())
            throw new ParseException(PE_MSG_PASSWORD_PATTERN, PARSE_COD_PASSWORD_PATTERN);
        
        return true;
    }

    //Same pass
    public static boolean isValidSamePasswords(String password, String samePass) throws ParseException {
        if (!password.equalsIgnoreCase(samePass))
            throw new ParseException(PE_MSG_PASSWORDS_DINDT_MATCH, PARSE_COD_PASSWORDS_DINDT_MATCH);

        return true;
    }

    //Fecha Nacimiento
    public static boolean isValidBirthday(Date fechaNacimiento) throws ParseException {
        Calendar calendarMayorEdad = Calendar.getInstance();

        int anio = Calendar.getInstance().get(Calendar.YEAR) - BIRTHDAY_MIN_AGE;
        calendarMayorEdad.set(Calendar.YEAR, anio);

        Date fechaMayorEdad = calendarMayorEdad.getTime();

        int resCompareDates = fechaNacimiento.compareTo(fechaMayorEdad);

        if (resCompareDates > 0)
            throw new ParseException(PE_MSG_BIRTHDAY_MENOR_EDAD, PARSE_COD_BIRTHDAY_MENOR_EDAD);

        return true;//<=
    }

    // ---- PROCESAR ---- //

    public ParserResponse processEmail(String email) {
        ParserResponse response = null;
    	String msg = null;
        boolean emailOk = false;

        try {
        	emailOk = (
            		Parser.isNotNull(email)
                    && Parser.isNotEmptyString(email)
                    && UserParser.isValidEmailLength(email)
                    && UserParser.isValidEmail(email)
            );
        } catch(ParseException pe) {
           msg = pe.getMessage();
        }

        if(emailOk) {
        	Map<String, Object> args = new HashMap<String, Object>();
        	args.put("email", email);
        	response = new ParserResponse().parserOKResponse("", args);
        }
        else {
        	response = new ParserResponse().parserFailResponse(msg, null);
        }

        return response;
    }

    public ParserResponse processName(String name) {
        ParserResponse response = null;
    	String msg = null;
    	boolean nameOk = false;

        try {
        	nameOk = (
            		Parser.isNotNull(name)
                    && Parser.isNotEmptyString(name)
                    && UserParser.isValidName(name)
            );
        } catch(ParseException pe) {
        	 msg = pe.getMessage();
        }
        
        if(nameOk) {
        	Map<String, Object> args = new HashMap<String, Object>();
        	args.put("name", name);
        	response = new ParserResponse().parserOKResponse("", args);
        }
        else {
        	response = new ParserResponse().parserFailResponse(msg, null);
        }

        return response;
    }

    public ParserResponse processPassword(String password) {
        ParserResponse response = null;
    	String msg = null;
        boolean passwordOK = false;

        try {
            passwordOK = (
            		Parser.isNotNull(password)
                    && Parser.isNotEmptyString(password)
                    && UserParser.isValidPassword(password)
            );
        } catch(ParseException pe) {
            msg = pe.getMessage();
        }

        if(passwordOK) {
        	Map<String, Object> args = new HashMap<String, Object>();
        	args.put("password", password);
        	response = new ParserResponse().parserOKResponse("", args);
        }
        else {
        	response = new ParserResponse().parserFailResponse(msg, null);
        }

        return response;
    }

    public ParserResponse processPasswordAndSamePass(String password, String samePass) {
        ParserResponse response = null;
    	String msg = null;
    	boolean samePasswordOK = false;

        try {
            samePasswordOK = (
            		Parser.isNotNull(samePass)
                    && Parser.isNotEmptyString(samePass)
                    && UserParser.isValidPassword(samePass)
                    && UserParser.isValidSamePasswords(password, samePass)
            );
        } catch(ParseException pe) {
        	msg = pe.getMessage();
        }

        if(samePasswordOK) {
        	Map<String, Object> args = new HashMap<String, Object>();
        	args.put("password", password);
        	response = new ParserResponse().parserOKResponse("", args);
        }
        else {
        	response = new ParserResponse().parserFailResponse(msg, null);
        }

        return response;
    }

    public ParserResponse processBirthday(Date birthday) {
        ParserResponse response = null;
    	String msg = null;
    	boolean birthdayOk = false;

        try {
            birthdayOk = UserParser.isValidBirthday(birthday);
        } catch(ParseException pe) {
        	msg = pe.getMessage();
        }
        
        if(birthdayOk) {
        	Map<String, Object> args = new HashMap<String, Object>();
        	args.put("birthday", birthday);
        	response = new ParserResponse().parserOKResponse("", args);
        }
        else {
        	response = new ParserResponse().parserFailResponse(msg, null);
        }

        return response;
    }
    
    public boolean parseUserRegister(ModelAndView modelAndView, UserTransfer userTransfer) {
    	ParserResponse responseEmail = this.processEmail(userTransfer.getEmail());
		
		if(!responseEmail.isOk()) {
			modelAndView.addObject("emailError", responseEmail.getMessage());
		}
		
		ParserResponse responseName = this.processName(userTransfer.getNickname());
		
		if(!responseName.isOk()) {
			modelAndView.addObject("nickNameError", responseName.getMessage());
		}
		
		ParserResponse responsePassword = null;
		if(userTransfer.getPassword() != null && userTransfer.getPassword() != "") {
			responsePassword = this.processPassword(userTransfer.getPassword());
			
			if(!responsePassword.isOk()) {
				modelAndView.addObject("passwordError", responsePassword.getMessage());
			}
		}
		
		ParserResponse responseSamePassword = null;
		if(userTransfer.getSamePassword() != null && userTransfer.getSamePassword() != "") {
			responseSamePassword = this.processPasswordAndSamePass(userTransfer.getPassword(), userTransfer.getSamePassword());
			
			if(!responseSamePassword.isOk()) {
				modelAndView.addObject("samePasswordError", responseSamePassword.getMessage());
			}
		}
		
		return responseEmail.isOk() 
				&& responseName.isOk() 
				&& (responsePassword != null ? responsePassword.isOk() : true) 
				&& (responseSamePassword != null ? responseSamePassword.isOk() : true);
    }
    
    public boolean parseUserModify(ModelAndView modelAndView, UserTransfer user) {
    	
		ParserResponse responseEmail = this.processEmail(user.getEmail());
		
		if(!responseEmail.isOk()) {
			modelAndView.addObject("emailError", responseEmail.getMessage());
		}
		
		ParserResponse responseName = this.processName(user.getName());
		
		if(!responseName.isOk()) {
			modelAndView.addObject("nameError", responseName.getMessage());
		}
		
		ParserResponse responseLastName = this.processName(user.getLastName());
		
		if(!responseLastName.isOk()) {
			modelAndView.addObject("lastNameError", responseLastName.getMessage());
		}
		
		ParserResponse responseBirthday = this.processBirthday(user.getBirthdate());
		
		if(!responseBirthday.isOk()) {
			modelAndView.addObject("birthdateError", responseBirthday.getMessage());
		}
		
		return responseEmail.isOk() 
				&& responseName.isOk() 
				&& responseLastName.isOk() 
				&& responseBirthday.isOk();
    }
    
    public boolean userLoginDataCorrect(ModelAndView modelAndView, UserTransfer userLogin) {
    	
    	ParserResponse pResEmail = new ParserResponse();
    	
    	if(userLogin.getEmail() != null) {
    		pResEmail = processEmail(userLogin.getEmail());
    		
    		if(!pResEmail.isOk()) {
    			modelAndView.addObject("emailError", pResEmail.getMessage());
    		}
    	}
    	
    	ParserResponse pResPassw = new ParserResponse();
    	
    	if(userLogin.getPassword() != null) {
    		pResPassw = processPassword(userLogin.getPassword());
    		
    		if(!pResPassw.isOk()) {
    			modelAndView.addObject("passwordError", pResPassw.getMessage());
    		}
    	}
    	
    	return pResEmail.isOk() && pResPassw.isOk();
    }

}
