package edu.ucdavis.dss.ipa.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import edu.ucdavis.dss.ipa.entities.Instructor;
import edu.ucdavis.dss.ipa.entities.SectionGroup;
import edu.ucdavis.dss.ipa.entities.TeachingAssignment;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TeachingAssignmentRepository extends CrudRepository<TeachingAssignment, Long> {

	TeachingAssignment findById(Long id);

	TeachingAssignment findOneBySectionGroupAndInstructor(SectionGroup sectionGroup, Instructor instructor);

	TeachingAssignment findOneByInstructorIdAndScheduleIdAndTermCodeAndBuyoutAndAndCourseReleaseAndSabbatical(
			Long instructorId, Long scheduleId, String termCode, Boolean buyout, Boolean courseRelease, Boolean sabbatical);

	List<TeachingAssignment> findByScheduleIdAndInstructorId(long scheduleId, long instructorId);

	@Query("SELECT ta FROM TeachingAssignment ta WHERE ta.sectionGroup IS NOT NULL AND ta.sectionGroup.course.id = :courseId")
	List<TeachingAssignment> findByCourseId(@Param("courseId") long courseId);

	@Modifying
	@Transactional
	@Query(value="delete from TeachingAssignment ta WHERE ta.id = ?1")
	void deleteById(long teachingAssignmentId);
}