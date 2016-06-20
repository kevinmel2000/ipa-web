package edu.ucdavis.dss.ipa.api.components.teachingCall.views.factories;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import edu.ucdavis.dss.ipa.entities.*;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.View;

import edu.ucdavis.dss.ipa.entities.Course;
import edu.ucdavis.dss.ipa.services.InstructorService;
import edu.ucdavis.dss.ipa.services.ScheduleInstructorNoteService;
import edu.ucdavis.dss.ipa.services.ScheduleService;
import edu.ucdavis.dss.ipa.services.ScheduleTermStateService;
import edu.ucdavis.dss.ipa.services.TeachingCallReceiptService;
import edu.ucdavis.dss.ipa.services.TeachingCallResponseService;
import edu.ucdavis.dss.ipa.services.UserRoleService;
import edu.ucdavis.dss.ipa.services.WorkgroupService;
import edu.ucdavis.dss.ipa.api.components.teachingCall.views.TeachingCallByCourseView;
import edu.ucdavis.dss.ipa.api.components.teachingCall.views.TeachingCallByInstructorView;
import edu.ucdavis.dss.ipa.api.components.teachingCall.views.TeachingCallCourseOfferingView;
import edu.ucdavis.dss.ipa.api.components.teachingCall.views.TeachingCallInstructorView;
import edu.ucdavis.dss.ipa.api.components.teachingCall.views.TeachingCallScheduleView;
import edu.ucdavis.dss.ipa.api.components.teachingCall.views.TeachingCallSummaryView;
import edu.ucdavis.dss.ipa.api.components.teachingCall.views.TeachingPreferencesExcelView;

@Service
public class JpaTeachingCallViewFactory implements TeachingCallViewFactory {
	@Inject WorkgroupService workgroupService;
	@Inject ScheduleTermStateService scheduleTermStateService;
	@Inject ScheduleService scheduleService;
	@Inject UserRoleService userRoleService;
	@Inject InstructorService instructorService;
	@Inject ScheduleInstructorNoteService scheduleInstructorNoteService;
	@Inject TeachingCallResponseService teachingCallResponseService;
	@Inject TeachingCallReceiptService teachingCallReceiptService;

	@Override
	public TeachingCallScheduleView createTeachingCallScheduleView(Schedule schedule) {
		TeachingCallScheduleView teachingCallScheduleView = new TeachingCallScheduleView();

		teachingCallScheduleView.setImporting(schedule.isImporting());
		teachingCallScheduleView.setId(schedule.getId());
		teachingCallScheduleView.setWorkgroupId(schedule.getWorkgroup().getId());
		teachingCallScheduleView.setYear(schedule.getYear());
		teachingCallScheduleView.setTeachingCalls(schedule.getTeachingCalls());
		List<String> terms = scheduleService.getActiveTermCodesForSchedule(schedule);
		teachingCallScheduleView.setScheduleTermStates(scheduleTermStateService.getScheduleTermStatesBySchedule(schedule));
		teachingCallScheduleView.setTerms(terms);

		return teachingCallScheduleView;
	}

	@Override
	public List<TeachingCallCourseOfferingView> createTeachingCallCourseOfferingView(Schedule schedule) {
		List<TeachingCallCourseOfferingView> teachingCallCourseOfferingViews = new ArrayList<TeachingCallCourseOfferingView>();

		for (Course course : schedule.getCourses()) {
			for (CourseOffering courseOffering : course.getSectionGroups()) {
				TeachingCallCourseOfferingView teachingCallCourseOfferingView = new TeachingCallCourseOfferingView(courseOffering);
				teachingCallCourseOfferingViews.add(teachingCallCourseOfferingView);
			}
		}

		return teachingCallCourseOfferingViews;
	}

	@Override
	public List<TeachingCallByInstructorView> createTeachingCallByInstructorView(Schedule schedule) {
		List<TeachingCallByInstructorView> teachingCallByInstructorViews = new ArrayList<TeachingCallByInstructorView>();
		List<Instructor> workgroupInstructors = userRoleService.getInstructorsByWorkgroupId(schedule.getWorkgroup().getId());

		for (Instructor instructor: workgroupInstructors) {
			List<TeachingCallResponse> teachingCallResponses = new ArrayList<>();
			TeachingCallReceipt teachingCallReceipt = null;

			for (TeachingCall teachingCall: schedule.getTeachingCalls()) {
				// Skip if this instructor was already processed in another teachingCall
				if (teachingCallResponses.size() > 0 || teachingCallReceipt != null) { continue; }

				teachingCallResponses = teachingCallResponseService.findByTeachingCallAndInstructorLoginId(teachingCall, instructor.getLoginId());
				teachingCallReceipt = teachingCallReceiptService.findByTeachingCallIdAndInstructorLoginId(teachingCall.getId(), instructor.getLoginId());
			}

			List<TeachingPreference> teachingPreferences = teachingPreferenceService.getTeachingPreferencesByScheduleIdAndInstructorId(schedule.getId(), instructor.getId());
			ScheduleInstructorNote scheduleInstructorNote = scheduleInstructorNoteService.findOneByInstructorIdAndScheduleId(instructor.getId(), schedule.getId());

			teachingCallByInstructorViews.add(
					new TeachingCallByInstructorView(
							instructor,
							teachingPreferences,
							teachingCallResponses,
							teachingCallReceipt,
							scheduleInstructorNote,
							schedule.getYear()
						)
				);
		}

		return teachingCallByInstructorViews;
	}
	
	@Override
	public List<TeachingCallByCourseView> createTeachingCallByCourseView(Schedule schedule) {
		List<TeachingCallByCourseView> teachingCallByCourseViews = new ArrayList<TeachingCallByCourseView>();

		for (Course cog : schedule.getCourses()) {
			List<TeachingPreference> teachingPreferences = teachingPreferenceService.getTeachingPreferencesByCourseOfferingGroupId(cog.getId());
			teachingCallByCourseViews.add(new TeachingCallByCourseView(cog, teachingPreferences));
		}

		return teachingCallByCourseViews;
	}

	@Override
	public List<TeachingCallInstructorView> createWorkgroupInstructors(Workgroup workgroup) {
		List<TeachingCallInstructorView> instructors = new ArrayList<TeachingCallInstructorView>();

		for (UserRole userRole: workgroup.getUserRoles()) {
			if (UserRole.isInstructor(userRole)) {
				Instructor instructor = instructorService.getOneByLoginId(userRole.getUser().getLoginId());
				TeachingCallInstructorView teachingCallInstructorView = new TeachingCallInstructorView(instructor);

				if (!instructors.contains(teachingCallInstructorView)) {
					instructors.add(new TeachingCallInstructorView(instructor));
				}
			}
		}

		return instructors;
	}

	@Override
	public TeachingCallSummaryView createTeachingCallSummaryView(TeachingCall teachingCall) {
		List<String> terms = scheduleService.getActiveTermCodesForSchedule(teachingCall.getSchedule());
		TeachingCallSummaryView teachingCallSummaryView = new TeachingCallSummaryView(teachingCall, terms);
		return teachingCallSummaryView;
	}

	@Override
	public View createTeachingPreferencesExcelView(List<TeachingCallByInstructorView> TeachingCallInstructors) {
		return new TeachingPreferencesExcelView(TeachingCallInstructors);
	}
}
