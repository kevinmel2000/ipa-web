package edu.ucdavis.dss.ipa.api.components.site;

import edu.ucdavis.dss.ipa.entities.User;
import edu.ucdavis.dss.ipa.security.Authorization;
import edu.ucdavis.dss.ipa.services.UserService;
import edu.ucdavis.dss.ipa.utilities.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class SiteController {
	private static final Logger log = LoggerFactory.getLogger("edu.ucdavis.ipa");
	private final int JS_EXCEPTION_MAIL_MAX_SUBJECT_LENGTH = 75;

	@Inject UserService userService;
	@Inject EmailService emailService;
	@Inject Authorization authorization;

	/**
	 * Provide /status.json for uptime checks. Designed to return
	 * HTTP 200 OK and the text "ok".
	 */
	@RequestMapping(value = "/status.json", method = RequestMethod.GET)
	@ResponseBody
	public HashMap<String, String> status(HttpServletResponse httpResponse) {
		HashMap<String,String> status = new HashMap<>();

		status.put("status", "ok");
		httpResponse.setStatus(HttpStatus.OK.value());

		return status;
	}

	/**
	 * Used by the JS front-end to report JS errors. This method will then
	 * e-mail those errors so they can be reported along with backend exceptions.
	 *
	 * @param exception
	 * @param httpResponse
	 * @throws MessagingException
	 */
	@RequestMapping(value = "/api/reportJsException", method = RequestMethod.POST)
	public void reportJsException(@RequestBody HashMap<String,String> exception, HttpServletResponse httpResponse)
			throws MessagingException {
		httpResponse.setStatus(HttpStatus.OK.value());

		// Generate e-mail subject from exception body itself (we have nothing else).
		// This tends to be long, so truncate it to character length of
		// JS_EXCEPTION_MAIL_MAX_SUBJECT_LENGTH.
		String messageSubject = exception.get("message");

		if (messageSubject == null) {
			messageSubject = "";
		}

		if(messageSubject.length() > JS_EXCEPTION_MAIL_MAX_SUBJECT_LENGTH) {
			int wordBoundaryIndex = messageSubject.lastIndexOf(' ', JS_EXCEPTION_MAIL_MAX_SUBJECT_LENGTH - 3);
			if(wordBoundaryIndex == -1) {
				messageSubject = messageSubject.substring(0, JS_EXCEPTION_MAIL_MAX_SUBJECT_LENGTH - 3) + "...";
			}
			messageSubject = messageSubject.substring(0, wordBoundaryIndex) + "...";
		}

		// Construct the email body
		List<String> body = new ArrayList<String>();

		User user = userService.getOneByLoginId(authorization.getLoginId());
		String displayName = "N/A";
		String kerberosName = "N/A";

		if (user != null) {
			displayName = user.getName();
			kerberosName = user.getLoginId();
		}

		body.add("URL: " + exception.get("url"));
		body.add("User: " + displayName);
		body.add("Kerberos: " + kerberosName);
		body.add("Full Error: " + exception.get("message"));
		body.add("Stack: " + exception.get("stack"));

		String messageBody = String.join("\n\n", body);
		messageSubject = "JS Exception: " + messageSubject;

		// Log this exception to log4j (we may also e-mail it)
		log.error(messageSubject);
		log.error(messageBody);

		emailService.send("dssit-devs-exceptions@ucdavis.edu", messageBody, messageSubject, false);
	}

	/**
	 * Takes a standard form POST and sends an e-mail.
	 *
	 * Receives 'name', 'email', and 'message'.
	 *
	 * May need more advanced protection if it begins to receive spam.
	 *
	 * @param name
	 * @param email
	 * @param message
	 * @param httpResponse
	 * @throws MessagingException
	 */
	@RequestMapping(value = "/contactFormSubmission", method = RequestMethod.POST)
	public void contactFormSubmission(@ModelAttribute(value="name") String name,
									  @ModelAttribute(value="email") String email,
									  @ModelAttribute(value="message") String message,
									  HttpServletResponse httpResponse)
			throws MessagingException {

		if((name == null) || (email == null) || (message == null)) {
			httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
			return;
		}

		httpResponse.setStatus(HttpStatus.OK.value());

		// Construct the email body
		List<String> body = new ArrayList<String>();

		body.add("Name    : " + name);
		body.add("Email   : " + email);
		body.add("Message : " + message.replaceAll("(\r\n|\n)", "<br />"));

		String messageBody = String.join("\n\n", body);

		emailService.send("dssit-devs@ucdavis.edu", messageBody, "IPA Public Contact Form Submission");
	}
}
