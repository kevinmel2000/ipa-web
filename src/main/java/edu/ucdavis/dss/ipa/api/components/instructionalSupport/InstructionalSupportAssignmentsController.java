package edu.ucdavis.dss.ipa.api.components.instructionalSupport;

import edu.ucdavis.dss.ipa.api.components.instructionalSupport.views.InstructionalSupportAssignmentView;
import edu.ucdavis.dss.ipa.api.components.instructionalSupport.views.factories.InstructionalSupportViewFactory;
import edu.ucdavis.dss.ipa.entities.*;
import edu.ucdavis.dss.ipa.security.Authorization;
import edu.ucdavis.dss.ipa.security.Authorizer;
import edu.ucdavis.dss.ipa.services.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class InstructionalSupportAssignmentsController {
    @Inject InstructionalSupportViewFactory instructionalSupportViewFactory;
    @Inject SectionGroupService sectionGroupService;
    @Inject SupportAssignmentService supportAssignmentService;
    @Inject SupportStaffService supportStaffService;
    @Inject Authorizer authorizer;
    @Inject SectionService sectionService;

    @RequestMapping(value = "/api/instructionalSupportView/workgroups/{workgroupId}/years/{year}/termCode/{shortTermCode}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public InstructionalSupportAssignmentView getInstructionalSupportAssignmentView(@PathVariable long workgroupId, @PathVariable long year, @PathVariable String shortTermCode) {
        authorizer.hasWorkgroupRoles(workgroupId, "academicPlanner", "reviewer", "senateInstructor", "federationInstructor", "studentPhd", "studentMasters", "instructionalSupport", "lecturer");

        return instructionalSupportViewFactory.createAssignmentView(workgroupId, year, shortTermCode);
    }

    @RequestMapping(value = "/api/instructionalSupportView/instructionalSupportAssignments/{instructionalSupportAssignmentId}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    public Long deleteAssignment(@PathVariable long instructionalSupportAssignmentId, HttpServletResponse httpResponse) {
        SupportAssignment supportAssignment = supportAssignmentService.findOneById(instructionalSupportAssignmentId);

        Workgroup workgroup = supportAssignment.getSectionGroup().getCourse().getSchedule().getWorkgroup();
        authorizer.hasWorkgroupRole(workgroup.getId(), "academicPlanner");

        // Ensure the assignment is unassigned.
        if (supportAssignment.getSupportStaff() != null) {
            httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return null;
        }

        supportAssignmentService.delete(instructionalSupportAssignmentId);

        return instructionalSupportAssignmentId;
    }

    @RequestMapping(value = "/api/instructionalSupportView/sectionGroups/{sectionGroupId}/assignmentType/{type}/supportStaff/{supportStaffId}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public SupportAssignment assignStaffToSectionGroup(@PathVariable long sectionGroupId, @PathVariable String type, @PathVariable long supportStaffId, HttpServletResponse httpResponse) {
        SectionGroup sectionGroup = sectionGroupService.getOneById(sectionGroupId);

        Workgroup workgroup = sectionGroup.getCourse().getSchedule().getWorkgroup();
        authorizer.hasWorkgroupRole(workgroup.getId(), "academicPlanner");

        SupportStaff supportStaff = supportStaffService.findOneById(supportStaffId);

        if (supportStaff == null) {
            httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return null;
        }

        SupportAssignment supportAssignment = new SupportAssignment();
        supportAssignment.setAppointmentType(type);
        supportAssignment.setSectionGroup(sectionGroup);
        supportAssignment.setSupportStaff(supportStaff);

        return supportAssignmentService.save(supportAssignment);
    }

    @RequestMapping(value = "/api/instructionalSupportView/sections/{sectionId}/assignmentType/{type}/supportStaff/{supportStaffId}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public SupportAssignment assignStaffToSection(@PathVariable long sectionId, @PathVariable String type, @PathVariable long supportStaffId, HttpServletResponse httpResponse) {
        Section section = sectionService.getOneById(sectionId);

        Workgroup workgroup = section.getSectionGroup().getCourse().getSchedule().getWorkgroup();
        authorizer.hasWorkgroupRole(workgroup.getId(), "academicPlanner");

        SupportStaff supportStaff = supportStaffService.findOneById(supportStaffId);

        if (supportStaff == null) {
            httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return null;
        }

        SupportAssignment supportAssignment = new SupportAssignment();
        supportAssignment.setAppointmentType(type);
        supportAssignment.setSection(section);
        supportAssignment.setSupportStaff(supportStaff);

        return supportAssignmentService.save(supportAssignment);
    }
}