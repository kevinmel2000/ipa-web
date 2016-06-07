package edu.ucdavis.dss.ipa.web.controllers.api;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.ucdavis.dss.ipa.config.annotation.WebController;
import edu.ucdavis.dss.ipa.entities.Instructor;
import edu.ucdavis.dss.ipa.entities.Schedule;
import edu.ucdavis.dss.ipa.entities.ScheduleInstructorNote;
import edu.ucdavis.dss.ipa.services.InstructorService;
import edu.ucdavis.dss.ipa.services.ScheduleInstructorNoteService;
import edu.ucdavis.dss.ipa.services.ScheduleService;

@WebController
public class ScheduleInstructorNoteController {
	@Inject ScheduleInstructorNoteService scheduleInstructorNoteService;
	@Inject InstructorService instructorService;
	@Inject ScheduleService scheduleService;

	/**
	 * Used in the Teaching Preferences AC view to toggle AssignmentsCompleted boolean flag
	 * @param scheduleId
	 * @param instructorId
	 * @param httpResponse
	 * @return
	 */
	@PreAuthorize("hasPermission(#scheduleId, 'schedule', 'academicCoordinator')")
	@RequestMapping(value = "/api/schedules/{scheduleId}/instructors/{instructorId}", method = RequestMethod.POST, produces="application/json")
	@ResponseBody
	public ScheduleInstructorNote toggleAssignmentsCompleted(
			@PathVariable long scheduleId,
			@PathVariable long instructorId,
			HttpServletResponse httpResponse) {
		Instructor instructor = instructorService.getInstructorById(instructorId);
		Schedule schedule = scheduleService.findById(scheduleId);
		if (instructor == null || schedule == null) {
			httpResponse.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
			return null;
		}

		ScheduleInstructorNote toBeSaved =  scheduleInstructorNoteService.findOrCreateOneByInstructorAndSchedule(instructor, schedule);
		 toBeSaved.setAssignmentsCompleted(!toBeSaved.getAssignmentsCompleted());

		return scheduleInstructorNoteService.saveScheduleInstructorNote(toBeSaved);
	}

}
