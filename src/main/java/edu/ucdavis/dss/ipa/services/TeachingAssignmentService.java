package edu.ucdavis.dss.ipa.services;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import edu.ucdavis.dss.ipa.entities.Instructor;
import edu.ucdavis.dss.ipa.entities.SectionGroup;
import edu.ucdavis.dss.ipa.entities.TeachingAssignment;

@Validated
public interface TeachingAssignmentService {
	TeachingAssignment saveTeachingAssignment(@NotNull @Valid TeachingAssignment teachingAssignment);

	void deleteTeachingAssignmentById(Long id);

	TeachingAssignment findOneById(Long id);

	TeachingAssignment findOrCreateOneBySectionGroupAndInstructor(SectionGroup sectionGroup, Instructor instructor);
}
