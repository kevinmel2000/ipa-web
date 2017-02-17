package edu.ucdavis.dss.ipa.api.components.teachingCallResponseReport.views.factories;

import edu.ucdavis.dss.ipa.api.components.teachingCallResponseReport.views.TeachingCallResponseReportView;
import edu.ucdavis.dss.ipa.entities.*;
import edu.ucdavis.dss.ipa.services.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
public class JpaTeachingCallResponseReportViewFactory implements TeachingCallResponseReportViewFactory {

    @Inject ScheduleService scheduleService;

    @Inject CourseService courseService;
    @Inject SectionGroupService sectionGroupService;
    @Inject SectionService sectionService;
    @Inject ActivityService activityService;

    @Inject TeachingAssignmentService teachingAssignmentService;
    @Inject InstructorService instructorService;
    @Inject UserRoleService userRoleService;

    @Override
    public TeachingCallResponseReportView createTeachingCallResponseReportView(long workgroupId, long year) {
        Schedule schedule = scheduleService.findByWorkgroupIdAndYear(workgroupId, year);

        List<TeachingAssignment> teachingAssignments = schedule.getTeachingAssignments();
        List<Course> courses = schedule.getCourses();
        List<SectionGroup> sectionGroups = sectionGroupService.findByWorkgroupIdAndYear(workgroupId, year);

        List<TeachingCallResponse> teachingCallResponses = schedule.getTeachingCallResponses();
        List<TeachingCallReceipt> teachingCallReceipts = new ArrayList<>();
        List<Instructor> instructors = new ArrayList<>();

        for (TeachingAssignment teachingAssignment : teachingAssignments) {
            Instructor instructor = teachingAssignment.getInstructor();

            // Only add new instances of the instructor
            if (instructors.indexOf(instructor) == -1) {
                instructors.add(instructor);
            }
        }

        return new TeachingCallResponseReportView(courses, sectionGroups, teachingAssignments, teachingCallReceipts, teachingCallResponses, instructors);
    }
}
