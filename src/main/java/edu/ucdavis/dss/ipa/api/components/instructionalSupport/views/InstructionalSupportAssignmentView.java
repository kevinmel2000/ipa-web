package edu.ucdavis.dss.ipa.api.components.instructionalSupport.views;

import edu.ucdavis.dss.ipa.entities.*;

import java.util.List;

public class InstructionalSupportAssignmentView {
    List<SectionGroup> sectionGroups;
    List<Course> courses;
    List<InstructionalSupportAssignment> instructionalSupportAssignments;
    List<SupportStaff> supportStaffList;
    List<Long> mastersStudentIds;
    List<Long> phdStudentIds;
    List<Long> instructionalSupportIds;
    List<StudentInstructionalSupportPreference> studentInstructionalSupportPreferences;
    List<StudentInstructionalSupportCallResponse> studentInstructionalSupportCallResponses;
    Schedule schedule;

    public InstructionalSupportAssignmentView(List<SectionGroup> sectionGroups,
                                              List<Course> courses,
                                              List<InstructionalSupportAssignment> instructionalSupportAssignments,
                                              List<SupportStaff> supportStaffList,
                                              List<Long> mastersStudentsIds,
                                              List<Long> phdStudentsIds,
                                              List<Long> instructionalSupportIds,
                                              List<StudentInstructionalSupportPreference> studentInstructionalSupportPreferences,
                                              List<StudentInstructionalSupportCallResponse> studentInstructionalSupportCallResponses,
                                              Schedule schedule) {
        setSectionGroups(sectionGroups);
        setCourses(courses);
        setInstructionalSupportAssignments(instructionalSupportAssignments);
        setSupportStaffList(supportStaffList);
        setMastersStudentIds(mastersStudentsIds);
        setPhdStudentIds(phdStudentsIds);
        setInstructionalSupportIds(instructionalSupportIds);
        setStudentInstructionalSupportPreferences(studentInstructionalSupportPreferences);
        setStudentInstructionalSupportCallResponses(studentInstructionalSupportCallResponses);
        setSchedule(schedule);
    }

    public List<SectionGroup> getSectionGroups() {
        return sectionGroups;
    }

    public void setSectionGroups(List<SectionGroup> sectionGroups) {
        this.sectionGroups = sectionGroups;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public List<InstructionalSupportAssignment> getInstructionalSupportAssignments() {
        return instructionalSupportAssignments;
    }

    public void setInstructionalSupportAssignments(List<InstructionalSupportAssignment> instructionalSupportAssignments) {
        this.instructionalSupportAssignments = instructionalSupportAssignments;
    }

    public List<SupportStaff> getSupportStaffList() {
        return supportStaffList;
    }

    public void setSupportStaffList(List<SupportStaff> supportStaffList) {
        this.supportStaffList = supportStaffList;
    }

    public List<Long> getMastersStudentIds() {
        return mastersStudentIds;
    }

    public void setMastersStudentIds(List<Long> mastersStudentIds) {
        this.mastersStudentIds = mastersStudentIds;
    }

    public List<Long> getPhdStudentIds() {
        return phdStudentIds;
    }

    public void setPhdStudentIds(List<Long> phdStudentIds) {
        this.phdStudentIds = phdStudentIds;
    }

    public List<Long> getInstructionalSupportIds() {
        return instructionalSupportIds;
    }

    public void setInstructionalSupportIds(List<Long> instructionalSupportIds) {
        this.instructionalSupportIds = instructionalSupportIds;
    }

    public List<StudentInstructionalSupportPreference> getStudentInstructionalSupportPreferences() {
        return studentInstructionalSupportPreferences;
    }

    public void setStudentInstructionalSupportPreferences(List<StudentInstructionalSupportPreference> studentInstructionalSupportPreferences) {
        this.studentInstructionalSupportPreferences = studentInstructionalSupportPreferences;
    }

    public List<StudentInstructionalSupportCallResponse> getStudentInstructionalSupportCallResponses() {
        return studentInstructionalSupportCallResponses;
    }

    public void setStudentInstructionalSupportCallResponses(List<StudentInstructionalSupportCallResponse> studentInstructionalSupportCallResponses) {
        this.studentInstructionalSupportCallResponses = studentInstructionalSupportCallResponses;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}