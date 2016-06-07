package edu.ucdavis.dss.ipa.web.controllers.api;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonView;

import edu.ucdavis.dss.ipa.config.annotation.WebController;
import edu.ucdavis.dss.ipa.entities.Instructor;
import edu.ucdavis.dss.ipa.entities.Workgroup;
import edu.ucdavis.dss.ipa.services.AuthenticationService;
import edu.ucdavis.dss.ipa.services.InstructorService;
import edu.ucdavis.dss.ipa.services.UserRoleService;
import edu.ucdavis.dss.ipa.services.WorkgroupService;
import edu.ucdavis.dss.ipa.web.components.summary.views.SummaryInstructorView;
import edu.ucdavis.dss.ipa.web.components.summary.views.factories.SummaryViewFactory;
import edu.ucdavis.dss.ipa.web.views.InstructorViews;

@WebController
public class InstructorController {
	@Inject InstructorService instructorService;
	@Inject AuthenticationService authenticationService;
	@Inject WorkgroupService workgroupService;
	@Inject UserRoleService userRoleService;
	@Inject SummaryViewFactory summaryViewFactory;

	@RequestMapping(value = "/api/workgroups/{id}/instructors", method = RequestMethod.GET)
	@ResponseBody
	@JsonView(InstructorViews.Detailed.class)
	// SECUREME
	@PreAuthorize("isAuthenticated()")
	public List<Instructor> getWorkgroupInstructors (@PathVariable Long id, HttpServletResponse httpResponse) {
		return this.userRoleService.getInstructorsByWorkgroupId(id);
	}

	@RequestMapping(value = "/api/instructors", method = RequestMethod.POST)
	@ResponseBody
	// SECUREME
	@PreAuthorize("isAuthenticated()")
	public Instructor addInstructor(@RequestBody Instructor instructor, HttpServletResponse httpResponse) {
		Instructor newInstructor = this.instructorService.saveInstructor(instructor);
		
		httpResponse.setStatus(HttpStatus.OK.value());
		
		return newInstructor;
	}

	@RequestMapping(value = "/api/workgroups/{workgroupId}/instructors/{instructorId}", method = RequestMethod.GET)
	@ResponseBody
	// SECUREME
	@PreAuthorize("isAuthenticated()")
	public SummaryInstructorView getInstructor (@PathVariable Long workgroupId, @PathVariable Long instructorId, HttpServletResponse httpResponse) {
		Instructor instructor = this.instructorService.getInstructorById(instructorId);
		Workgroup workgroup = this.workgroupService.findOneById(workgroupId);
		
		return summaryViewFactory.createSummaryInstructorView(instructor, workgroup);
	}
}
