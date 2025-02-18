package edu.ucdavis.dss.ipa.api.components.assignment.views;

import edu.ucdavis.dss.ipa.entities.Course;
import edu.ucdavis.dss.ipa.entities.Instructor;
import edu.ucdavis.dss.ipa.entities.Schedule;
import edu.ucdavis.dss.ipa.entities.ScheduleTermState;
import edu.ucdavis.dss.ipa.entities.SectionGroup;
import edu.ucdavis.dss.ipa.entities.Tag;
import edu.ucdavis.dss.ipa.entities.TeachingAssignment;
import edu.ucdavis.dss.ipa.entities.Term;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by okadri on 9/14/16.
 */
public class AssignmentExcelView extends AbstractXlsView {
    private Schedule schedule = null;
    private List<Instructor> instructors = new ArrayList<>();
    private List<ScheduleTermState> scheduleTermStates = new ArrayList<>();

    public AssignmentExcelView(Schedule schedule, List<Instructor> instructors, List<ScheduleTermState> scheduleTermStates) {
        this.schedule = schedule;
        this.instructors = instructors;
        this.scheduleTermStates = scheduleTermStates;
    }

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Set filename
        response.setHeader("Content-Type", "multipart/mixed; charset=\"UTF-8\"");
        String header = "attachment; filename=TeachingAssignments-" + schedule.getYear() + "-" + schedule.getWorkgroup().getName() + ".xls";
        response.setHeader("Content-Disposition", header);

        // Create sheets
        Sheet byCourseSheet = workbook.createSheet("By Course");
        Sheet byInstructorSheet = workbook.createSheet("By Instructor");

        setExcelHeader(byCourseSheet, "Course");
        setExcelHeader(byInstructorSheet, "Instructor");

        // By Course Sheet
        int row = 1;
        for(Course course : schedule.getCourses()) {
            Row excelHeader = byCourseSheet.createRow(row);
            int col = 0;

            excelHeader.createCell(col).setCellValue(course.getShortDescription());
            col++;

            excelHeader.createCell(col).setCellValue(course.getUnitsLow());
            col++;

            List<String> tagNameList = course.getTags().stream().map(Tag::getName).collect(Collectors.toList());

            excelHeader.createCell(col).setCellValue(StringUtils.join(tagNameList, ','));
            col++;

            for(ScheduleTermState state : scheduleTermStates) {
                SectionGroup sectionGroup = this.getSectionGroupByCourseAndTermCode(course, state.getTermCode());

                if (sectionGroup != null) {
                    List<String> instructorNames = new ArrayList<>();
                    for (TeachingAssignment teachingAssignment : sectionGroup.getTeachingAssignments()) {
                        if (teachingAssignment.isApproved() == false) { continue; }

                        String name = teachingAssignment.getInstructorDisplayName();

                        instructorNames.add(name);
                    }

                    String instructNamesFormatted = String.join(", ", instructorNames);
                    excelHeader.createCell(col).setCellValue(instructNamesFormatted);
                }

                col++;
            }

            row++;
        }

        // By Instructor Sheet
        row = 1;
        for(Instructor instructor : instructors) {
            Row excelHeader = byInstructorSheet.createRow(row);
            int col = 0;

            excelHeader.createCell(col).setCellValue(instructor.getLastName() + ", " + instructor.getFirstName());
            col++;

            for(ScheduleTermState state : scheduleTermStates) {
                List<String> assignmentDescriptions = new ArrayList<>();

                for (TeachingAssignment teachingAssignment : instructor.getTeachingAssignments()) {
                    if (teachingAssignment.isApproved() == false) { continue; }
                    if (teachingAssignment.getSchedule().getId() != this.schedule.getId()) { continue; }
                    if (state.getTermCode().equals(teachingAssignment.getTermCode()) == false) { continue; }

                    if (teachingAssignment.getInstructor().getId() == 16) {
                        System.out.println("taco");
                    }

                    assignmentDescriptions.add(teachingAssignment.getDescription());
                }

                String formattedAssignmentDescriptions = StringUtils.join(assignmentDescriptions, ", ");

                excelHeader.createCell(col).setCellValue(formattedAssignmentDescriptions);

                col++;
            }

            row++;
        }
    }

    private void setExcelHeader(Sheet excelSheet, String firstHeader) {
        Row excelHeader = excelSheet.createRow(0);
        int col = 0;

        excelHeader.createCell(col).setCellValue(firstHeader);
        col++;

        if ("Course".equals(firstHeader)) {
            excelHeader.createCell(col).setCellValue("Units");
            col++;

            excelHeader.createCell(col).setCellValue("Tracks");
            col++;
        }

        for(ScheduleTermState state : scheduleTermStates) {
            excelHeader.createCell(col).setCellValue(Term.getRegistrarName(state.getTermCode()));
            col++;
        }
    }

    private SectionGroup  getSectionGroupByCourseAndTermCode(Course course, String termCode) {
        Predicate<SectionGroup> predicate = sg-> sg.getTermCode().equals(termCode) && sg.getCourse().equals(course);
        String courseDesc = course.getShortDescription();
        List<SectionGroup> matchingSectionGroups = course.getSectionGroups().stream().filter(predicate).collect(Collectors.toList());

        if (matchingSectionGroups.size() > 0) {
            return matchingSectionGroups.get(0);
        } else {
            return null;
        }
    }

}
