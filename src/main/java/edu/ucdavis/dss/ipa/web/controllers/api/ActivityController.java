package edu.ucdavis.dss.ipa.web.controllers.api;

import java.util.ArrayList;
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

import edu.ucdavis.dss.ipa.config.annotation.WebController;
import edu.ucdavis.dss.ipa.entities.Activity;
import edu.ucdavis.dss.ipa.entities.Section;
import edu.ucdavis.dss.ipa.entities.SectionGroup;
import edu.ucdavis.dss.ipa.services.ActivityService;
import edu.ucdavis.dss.ipa.services.SectionGroupService;
import edu.ucdavis.dss.ipa.services.SectionService;
import edu.ucdavis.dss.ipa.web.components.term.views.TermActivityView;

@WebController
public class ActivityController {
	@Inject ActivityService activityService;
	@Inject SectionGroupService sectionGroupService;
	@Inject SectionService sectionService;

	@PreAuthorize("hasPermission(#id, 'activity', 'academicCoordinator')")
	@RequestMapping(value = "/api/activities/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteActivity(@PathVariable Long id, HttpServletResponse httpResponse)
	{
		List<Activity> activitiesToDelete = new ArrayList<Activity>();
		Activity activity = activityService.findOneById(id);

		if (activity.isShared()) {
			activitiesToDelete = this.activityService.findSharedActivitySet(id);
		} else {
			activitiesToDelete.add(activity);
		}

		for(Activity slotActivity : activitiesToDelete) {
			this.activityService.deleteActivityById(slotActivity.getId());
		}

		httpResponse.setStatus(HttpStatus.NO_CONTENT.value());
	}

	@RequestMapping(value = "/api/sections/{id}/activities", method = RequestMethod.POST)
	@ResponseBody
	@PreAuthorize("hasPermission(#id, 'section', 'academicCoordinator')")
	public TermActivityView createUniqueActivity(@RequestBody Activity activity, @PathVariable Long id, HttpServletResponse httpResponse) {
		Section section = sectionService.getSectionById(id);

		if (section == null) {
			httpResponse.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
			return null;
		} else {
			activity.setSection(section);
			activity.setShared(false);

			Activity newActivity = activityService.saveActivity(activity);
			List<Activity> activities = section.getActivities();
			activities.add(newActivity);
			section.setActivities(activities);
			sectionService.saveSection(section);

			httpResponse.setStatus(HttpStatus.OK.value());
			return new TermActivityView(newActivity);
		}
	}

	@PreAuthorize("hasPermission(#id, 'activity', 'academicCoordinator')")
	@RequestMapping(value = "/api/activities/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public Activity updateActivity( @RequestBody Activity activity, @PathVariable Long id, HttpServletResponse httpResponse_p) {
		Activity activityToReturn = null;
		List<Activity> activitiesToChange = new ArrayList<Activity>();
		Activity originalActivity = activityService.findOneById(id);

		if (activity.isShared()) {
			activitiesToChange = this.activityService.findSharedActivitySet(id);
		} else {
			activitiesToChange.add(activity);
		}

		for(Activity activityToChange : activitiesToChange) {
			activityToChange.setActivityTypeCode(activity.getActivityTypeCode());
			activityToChange.setBeginDate(activity.getBeginDate());
			activityToChange.setEndDate(activity.getEndDate());
			activityToChange.setBuilding(activity.getBuilding());
			activityToChange.setActivityState(activity.getActivityState());
			activityToChange.setDayIndicator(activity.getDayIndicator());
			activityToChange.setEndTime(activity.getEndTime());
			activityToChange.setRoom(activity.getRoom());
			activityToChange.setStartTime(activity.getStartTime());
			activityToChange.setVirtual(activity.isVirtual());
			activityToChange.setSection(originalActivity.getSection());

			if (activityToChange.getId() == id) {
				activityToReturn = activityToChange;
			}

			this.activityService.saveActivity(activityToChange);
		}

		httpResponse_p.setStatus(HttpStatus.OK.value());
		
		return activityToReturn;
	}
}