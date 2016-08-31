package edu.ucdavis.dss.ipa.api.components.scheduling;

import edu.ucdavis.dss.ipa.api.components.scheduling.views.SchedulingView;
import edu.ucdavis.dss.ipa.api.components.scheduling.views.SchedulingViewSectionGroup;
import edu.ucdavis.dss.ipa.api.components.scheduling.views.factories.SchedulingViewFactory;
import edu.ucdavis.dss.ipa.entities.Activity;
import edu.ucdavis.dss.ipa.entities.Section;
import edu.ucdavis.dss.ipa.entities.SectionGroup;
import edu.ucdavis.dss.ipa.entities.Workgroup;
import edu.ucdavis.dss.ipa.entities.enums.ActivityState;
import edu.ucdavis.dss.ipa.security.authorization.Authorizer;
import edu.ucdavis.dss.ipa.services.ActivityService;
import edu.ucdavis.dss.ipa.services.SectionGroupService;
import edu.ucdavis.dss.ipa.services.SectionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin // TODO: make CORS more specific depending on profile
public class SchedulingViewController {

	@Inject SectionGroupService sectionGroupService;
	@Inject SectionService sectionService;
	@Inject ActivityService activityService;
	@Inject SchedulingViewFactory schedulingViewFactory;

	/**
	 * Delivers the JSON payload for the Scheduling View (nee Activities View), used on page load.
	 *
	 * @param workgroupId
	 * @param year
	 * @param termCode
	 * @param httpResponse
     * @return
     */
	@RequestMapping(value = "/api/schedulingView/workgroups/{workgroupId}/years/{year}/termCode/{termCode}", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public SchedulingView showSchedulingView(@PathVariable long workgroupId, @PathVariable long year, @PathVariable String termCode,
										 @RequestParam(value="showDoNotPrint", required=false) Boolean showDoNotPrint,
										 HttpServletResponse httpResponse) {
		Authorizer.hasWorkgroupRole(workgroupId, "academicPlanner");

		return schedulingViewFactory.createSchedulingView(workgroupId, year, termCode, showDoNotPrint);
	}

	/**
	 * Delivers sectionGroup details children including sections and their child activities
	 *
	 * @param sectionGroupId
	 * @param httpResponse
	 * @return
	 */
	@RequestMapping(value = "/api/schedulingView/sectionGroups/{sectionGroupId}", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public SchedulingViewSectionGroup getSectionGroupDetails(@PathVariable long sectionGroupId,
															 HttpServletResponse httpResponse) {
		SectionGroup sectionGroup = sectionGroupService.getOneById(sectionGroupId);
		if (sectionGroup == null) {
			httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
			return null;
		}
		Authorizer.hasWorkgroupRole(sectionGroup.getCourse().getSchedule().getWorkgroup().getId(), "academicPlanner");

		return schedulingViewFactory.createSchedulingViewSectionGroup(sectionGroup);
	}

    @RequestMapping(value = "/api/schedulingView/activities/{activityId}", method = RequestMethod.PUT, produces="application/json")
    @ResponseBody
    public Activity updateActivity(@PathVariable long activityId, @RequestBody Activity activity, HttpServletResponse httpResponse) {
    	Activity originalActivity = activityService.findOneById(activityId);
		if (originalActivity == null) {
			httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
			return null;
		}
		SectionGroup sectionGroup = sectionGroupService.getOneById(originalActivity.getSectionGroupIdentification());
		Workgroup workgroup = sectionGroup.getCourse().getSchedule().getWorkgroup();
        Authorizer.hasWorkgroupRole(workgroup.getId(), "academicPlanner");


		Activity activityToReturn = null;
		List<Activity> activitiesToChange = new ArrayList<>();

		if (activity.isShared()) {
			activitiesToChange = this.activityService.findSharedActivitySet(activityId);
		} else {
			activitiesToChange.add(originalActivity);
		}

		for(Activity activityToChange : activitiesToChange) {
			activityToChange.setLocation(activity.getLocation());
			activityToChange.setActivityState(activity.getActivityState());
			activityToChange.setFrequency(activity.getFrequency());
			activityToChange.setDayIndicator(activity.getDayIndicator());
			activityToChange.setStartTime(activity.getStartTime());
			activityToChange.setEndTime(activity.getEndTime());

			if (activityToChange.getId() == activityId) {
				activityToReturn = activityToChange;
			}

			this.activityService.saveActivity(activityToChange);
		}

		return activityToReturn;
    }

	@RequestMapping(value = "/api/schedulingView/activities/{activityId}", method = RequestMethod.DELETE, produces="application/json")
	@ResponseBody
	public void deleteActivity(@PathVariable long activityId, HttpServletResponse httpResponse) {
		Activity activity = activityService.findOneById(activityId);
		if (activity == null) {
			httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
			return;
		}
		SectionGroup sectionGroup = sectionGroupService.getOneById(activity.getSectionGroupIdentification());
		Workgroup workgroup = sectionGroup.getCourse().getSchedule().getWorkgroup();
		Authorizer.hasWorkgroupRole(workgroup.getId(), "academicPlanner");

		this.activityService.deleteActivityById(activity.getId());
	}

	@RequestMapping(value = "/api/schedulingView/sectionGroups/{sectionGroupId}", method = RequestMethod.POST, produces="application/json")
	@ResponseBody
	public Activity createSharedActivity(@RequestBody Activity activity, @PathVariable Long sectionGroupId, HttpServletResponse httpResponse) {
		SectionGroup sectionGroup = sectionGroupService.getOneById(sectionGroupId);
		if (sectionGroup == null) {
			httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
			return null;
		}
		Authorizer.hasWorkgroupRole(sectionGroup.getCourse().getSchedule().getWorkgroup().getId(), "academicPlanner");

		Activity slotActivity = new Activity();
		slotActivity.setActivityTypeCode(activity.getActivityTypeCode());
		slotActivity.setActivityState(ActivityState.DRAFT);
		slotActivity.setDayIndicator("0000000");
		slotActivity.setSectionGroup(sectionGroup);

		slotActivity = activityService.saveActivity(slotActivity);

		return slotActivity;
	}

	@RequestMapping(value = "/api/schedulingView/sections/{sectionId}", method = RequestMethod.POST, produces="application/json")
	@ResponseBody
	public Activity createActivity(@RequestBody Activity activity, @PathVariable Long sectionId, HttpServletResponse httpResponse) {
		Section section = sectionService.getOneById(sectionId);
		if (section == null) {
			httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
			return null;
		}
		Authorizer.hasWorkgroupRole(section.getSectionGroup().getCourse().getSchedule().getWorkgroup().getId(), "academicPlanner");

		Activity newActivity = new Activity();
		newActivity.setActivityTypeCode(activity.getActivityTypeCode());
		newActivity.setActivityState(ActivityState.DRAFT);
		newActivity.setDayIndicator("0000000");
		newActivity.setSection(section);

		return activityService.saveActivity(newActivity);
	}
}
