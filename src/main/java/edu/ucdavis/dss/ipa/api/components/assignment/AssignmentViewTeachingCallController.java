package edu.ucdavis.dss.ipa.api.components.assignment;

import edu.ucdavis.dss.ipa.api.helpers.CurrentUser;
import edu.ucdavis.dss.ipa.entities.*;
import edu.ucdavis.dss.ipa.security.authorization.Authorizer;
import edu.ucdavis.dss.ipa.services.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Lloyd on 8/10/16.
 */
@RestController
@CrossOrigin // TODO: make CORS more specific depending on profile
public class AssignmentViewTeachingCallController {
    @Inject CurrentUser currentUser;
    @Inject AuthenticationService authenticationService;
    @Inject WorkgroupService workgroupService;
    @Inject ScheduleService scheduleService;
    @Inject CourseService courseService;
    @Inject TeachingAssignmentService teachingAssignmentService;
    @Inject SectionGroupService sectionGroupService;
    @Inject InstructorService instructorService;
    @Inject TeachingCallReceiptService teachingCallReceiptService;
    @Inject TeachingCallService teachingCallService;

    @ResponseBody
    @RequestMapping(value = "/api/assignmentView/{workgroupId}/{year}/teachingCalls", method = RequestMethod.POST, produces="application/json")
    public TeachingCall createTeachingCall(@PathVariable long workgroupId, @PathVariable long year, @RequestBody TeachingCall teachingCallDTO, HttpServletResponse httpResponse) {
        Schedule schedule = scheduleService.findByWorkgroupIdAndYear(workgroupId, year);

        if(schedule == null) {
            httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return null;
        }

        Authorizer.hasWorkgroupRole(workgroupId, "academicPlanner");

        TeachingCall teachingCall = teachingCallService.create(schedule.getId(), teachingCallDTO);
        if (teachingCall != null) {
            httpResponse.setStatus(HttpStatus.OK.value());
        } else {
            httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        }

        return teachingCall;
    }

    @RequestMapping(value = "/api/assignmentView/teachingCalls/{teachingCallId}", method = RequestMethod.DELETE, produces="application/json")
    @ResponseBody
    public TeachingCall removeTeachingCall(@PathVariable long teachingCallId, HttpServletResponse httpResponse) {
        TeachingCall teachingCall = teachingCallService.findOneById(teachingCallId);
        Workgroup workgroup = teachingCall.getSchedule().getWorkgroup();
        Authorizer.hasWorkgroupRoles(workgroup.getId(), "academicPlanner");

        if (teachingCall != null) {
            teachingCallService.deleteById(teachingCallId);
        }

        return teachingCall;
    }
}
