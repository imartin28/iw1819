package es.ucm.fdi.iw.control;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import es.ucm.fdi.iw.util.DateUtil;
import es.ucm.fdi.iw.util.StringUtil;

@Controller
public class MyErrorController implements ErrorController  {
 
	private static final Logger log = LogManager.getLogger(MyErrorController.class);
	
	@Autowired
    private DefaultErrorAttributes errorAttributes;
	
	@Value("#{ ('${server.error.include-stacktrace}' == 'always' || '${server.error.include-stacktrace}' == 'true') ? true : false }")
    private boolean includeStackTrace = false;
	
	/**
	 * Function to notify the current user a message from server
	 * the modal is located on nav.html in order to display a message in any view
	 * @param model
	 * @param errorTitle
	 * @param errorMsg
	 */
	private void notifyModal(ModelAndView modelAndView, String title, String msg) {
		if(title != null && title != "" && msg != null && msg != "") {
			modelAndView.addObject("title", StringUtil.convertToTitleCaseSplitting(title));
			modelAndView.addObject("msg", msg);
		}
	}
	
	// Total control - setup a model and return the view name yourself. Or
	// consider subclassing ExceptionHandlerExceptionResolver (see below).
	@ExceptionHandler(Exception.class)
	public ModelAndView handleException(HttpServletRequest req, Exception ex) {
		log.error("Request: " + req.getRequestURL() + " raised " + ex);
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("exception", ex);
		mav.addObject("url", req.getRequestURL());
		mav.setViewName("error");
		return mav;
	}
	
	@RequestMapping("/error")
	public ModelAndView handleError(ModelAndView modelAndView, WebRequest webRequest, HttpServletRequest request, HttpServletResponse response,
			@RequestParam("title") String title, @RequestParam("msg") String msg
	) {
	    
		modelAndView.setViewName("error");
	    modelAndView.addObject("url", request.getRequestURI());
	    
	    Map<String, Object> errorAttr = errorAttributes.getErrorAttributes(webRequest, includeStackTrace);
	    
	    Object statusObj = errorAttr.get("status");
	    if(statusObj != null) {
	    	Integer status = (Integer)statusObj;
		    modelAndView.addObject("status", status);
	    }
	    
	    Object errorObj = errorAttr.get("error");
	    if(errorObj != null) {
		    String error = (String)errorObj;
		    modelAndView.addObject("error", error);
	    }
	    
	    Object messageObj = errorAttr.get("message");
	    if(messageObj != null) {
		    String message = (String)messageObj;
		    modelAndView.addObject("message", message);
	    }
	    
	    Object timestampObj = errorAttr.get("timestamp");
	    if(timestampObj != null) {
	    	Date timestamp = (Date)timestampObj;
		    modelAndView.addObject("timestamp", DateUtil.getDateWithHourFormat().format(timestamp));
	    }
	    
	    Object traceObj = errorAttr.get("trace");
	    if(traceObj != null) {
		    String trace = (String)traceObj;
		    modelAndView.addObject("trace", trace);
	    }
        
	    if(title != null && msg != null) {
	    	notifyModal(modelAndView, title, msg);
	    }
	    
	    return modelAndView;
	}
	
    @Override
    public String getErrorPath() {
        return "/error";
    }
}