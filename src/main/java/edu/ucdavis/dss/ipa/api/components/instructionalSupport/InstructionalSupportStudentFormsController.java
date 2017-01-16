package edu.ucdavis.dss.ipa.api.components.instructionalSupport;

import edu.ucdavis.dss.ipa.api.components.instructionalSupport.views.InstructionalSupportAssignmentView;
import edu.ucdavis.dss.ipa.api.components.instructionalSupport.views.InstructionalSupportCallStudentFormView;
import edu.ucdavis.dss.ipa.api.components.instructionalSupport.views.factories.InstructionalSupportViewFactory;
import edu.ucdavis.dss.ipa.entities.*;
import edu.ucdavis.dss.ipa.security.Authorization;
import edu.ucdavis.dss.ipa.security.authorization.Authorizer;
import edu.ucdavis.dss.ipa.services.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin // TODO: make CORS more specific depending on profile
public class InstructionalSupportStudentFormsController {

    @Inject InstructionalSupportViewFactory instructionalSupportViewFactory;
    @Inject UserService userService;
    @Inject InstructorService instructorService;
    @Inject SectionGroupService sectionGroupService;
    @Inject InstructionalSupportAssignmentService instructionalSupportAssignmentService;
    @Inject InstructionalSupportStaffService instructionalSupportStaffService;
    @Inject StudentInstructionalSupportPreferenceService studentInstructionalSupportPreferenceService;
    @Inject StudentInstructionalSupportCallResponseService studentInstructionalSupportCallResponseService;
    @Inject ScheduleService scheduleService;

    @RequestMapping(value = "/api/instructionalSupportStudentFormView/workgroups/{workgroupId}/years/{year}/termCode/{shortTermCode}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public InstructionalSupportCallStudentFormView getInstructionalSupportCallStudentFormView(@PathVariable long workgroupId, @PathVariable long year, @PathVariable String shortTermCode, HttpServletResponse httpResponse) {
        Authorizer.hasWorkgroupRoles(workgroupId, "academicPlanner", "reviewer");

        User currentUser = userService.getOneByLoginId(Authorization.getLoginId());
        InstructionalSupportStaff instructionalSupportStaff = instructionalSupportStaffService.findByLoginId(currentUser.getLoginId());

        return instructionalSupportViewFactory.createStudentFormView(workgroupId, year, shortTermCode, instructionalSupportStaff.getId());
    }

    @RequestMapping(value = "/api/instructionalSupportStudentFormView/supportCalls/{supportCallId}/sectionGroups/{sectionGroupId}/preferenceType/{preferenceType}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public StudentInstructionalSupportPreference addPreference(@PathVariable long supportCallId, @PathVariable long sectionGroupId, @PathVariable String preferenceType, HttpServletResponse httpResponse) {
        Long workgroupId = sectionGroupService.getOneById(sectionGroupId).getCourse().getSchedule().getWorkgroup().getId();
        //Authorizer.hasWorkgroupRoles(workgroupId, );

        User currentUser = userService.getOneByLoginId(Authorization.getLoginId());
        InstructionalSupportStaff instructionalSupportStaff = instructionalSupportStaffService.findByLoginId(currentUser.getLoginId());

        return studentInstructionalSupportPreferenceService.create(instructionalSupportStaff.getId(), supportCallId, sectionGroupId, preferenceType, "");
    }

    @RequestMapping(value = "/api/instructionalSupportStudentFormView/studentSupportCallResponses/{studentSupportCallResponseId}", method = RequestMethod.PUT, produces = "application/json")
    @ResponseBody
    public StudentInstructionalSupportCallResponse updateStudentSupportCallResponse(@PathVariable long studentSupportCallResponseId, @RequestBody StudentInstructionalSupportCallResponse studentInstructionalSupportCallResponseDTO, HttpServletResponse httpResponse) {
        StudentInstructionalSupportCallResponse originalSupportCallResponse = studentInstructionalSupportCallResponseService.findOneById(studentSupportCallResponseId);
        Long workgroupId = originalSupportCallResponse.getStudentInstructionalSupportCall().getSchedule().getWorkgroup().getId();
        //Authorizer.hasWorkgroupRoles(workgroupId, );

        User currentUser = userService.getOneByLoginId(Authorization.getLoginId());
        InstructionalSupportStaff instructionalSupportStaff = instructionalSupportStaffService.findByLoginId(currentUser.getLoginId());

        originalSupportCallResponse.setGeneralComments(studentInstructionalSupportCallResponseDTO.getGeneralComments());
        originalSupportCallResponse.setTeachingQualifications(studentInstructionalSupportCallResponseDTO.getTeachingQualifications());
        originalSupportCallResponse.setSubmitted(studentInstructionalSupportCallResponseDTO.isSubmitted());

        return studentInstructionalSupportCallResponseService.update(originalSupportCallResponse);
    }

    @RequestMapping(value = "/api/instructionalSupportStudentFormView/schedules/{scheduleId}/terms/{termCode}", method = RequestMethod.PUT, produces = "application/json")
    @ResponseBody
    public List<Long> updatePreferencesOrder(@PathVariable long scheduleId, @PathVariable String termCode, @RequestBody List<Long> preferenceIdsParams, HttpServletResponse httpResponse) {
        Long workgroupId = scheduleService.findById(scheduleId).getWorkgroup().getId();
        Authorizer.hasWorkgroupRoles(workgroupId, "studentMasters", "studentPhd");

        User currentUser = userService.getOneByLoginId(Authorization.getLoginId());
        InstructionalSupportStaff instructionalSupportStaff = instructionalSupportStaffService.findByLoginId(currentUser.getLoginId());

        studentInstructionalSupportPreferenceService.updatePriorities(preferenceIdsParams);

        return preferenceIdsParams;
    }

    @RequestMapping(value = "/api/instructionalSupportStudentFormView/studentInstructionalSupportPreferences/{studentPreferenceId}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    public Long deletePreference(@PathVariable long studentPreferenceId, HttpServletResponse httpResponse) {
        //Long workgroupId = studentInstructionalSupportPreferenceService.findById(studentPreferenceId).getCourse().getSchedule().getWorkgroup().getId();
        //Authorizer.hasWorkgroupRoles(workgroupId, );

        User currentUser = userService.getOneByLoginId(Authorization.getLoginId());

        studentInstructionalSupportPreferenceService.delete(studentPreferenceId);

        return studentPreferenceId;
    }
}