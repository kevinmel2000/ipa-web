package edu.ucdavis.dss.ipa.api.components.instructionalSupport.views;

import edu.ucdavis.dss.ipa.entities.*;
import java.util.ArrayList;
import java.util.List;

public class InstructionalSupportCallStatusView {
    List<InstructionalSupportStaff> instructionalSupportStaffList;
    List<Long> mastersStudentIds;
    List<Long> phdStudentIds;
    List<Long> instructionalSupportIds;
    List<StudentInstructionalSupportCall> studentSupportCalls;
    List<InstructorInstructionalSupportCall> instructorSupportCalls;
    Long scheduleId;

    public InstructionalSupportCallStatusView(Long scheduleId,
                                              List<InstructionalSupportStaff> instructionalSupportStaffList,
                                              List<Long> mastersStudentsIds,
                                              List<Long> phdStudentsIds,
                                              List<Long> instructionalSupportIds,
                                              List<StudentInstructionalSupportCall> studentSupportCalls,
                                              List<InstructorInstructionalSupportCall> instructorSupportCalls) {

        setScheduleId(scheduleId);
        setInstructionalSupportStaffList(instructionalSupportStaffList);
        setMastersStudentIds(mastersStudentsIds);
        setPhdStudentIds(phdStudentsIds);
        setInstructionalSupportIds(instructionalSupportIds);
        setStudentSupportCalls(studentSupportCalls);
        setInstructorSupportCalls(instructorSupportCalls);
    }

    public List<InstructionalSupportStaff> getInstructionalSupportStaffList() {
        return instructionalSupportStaffList;
    }

    public void setInstructionalSupportStaffList(List<InstructionalSupportStaff> instructionalSupportStaffList) {
        this.instructionalSupportStaffList = instructionalSupportStaffList;
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

    public List<StudentInstructionalSupportCall> getStudentSupportCalls() {
        return studentSupportCalls;
    }

    public void setStudentSupportCalls(List<StudentInstructionalSupportCall> studentSupportCalls) {
        this.studentSupportCalls = studentSupportCalls;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public List<InstructorInstructionalSupportCall> getInstructorSupportCalls() {
        return instructorSupportCalls;
    }

    public void setInstructorSupportCalls(List<InstructorInstructionalSupportCall> instructorSupportCalls) {
        this.instructorSupportCalls = instructorSupportCalls;
    }
}