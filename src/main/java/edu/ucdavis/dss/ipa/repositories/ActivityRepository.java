package edu.ucdavis.dss.ipa.repositories;

import org.springframework.data.repository.CrudRepository;

import edu.ucdavis.dss.ipa.entities.Activity;

import java.util.List;

public interface ActivityRepository extends CrudRepository<Activity, Long> {

	void deleteAllBySectionId(long sectionId);

    List<Activity> findBySection_SectionGroup_Id(long sectionGroupId);

    List<Activity> findBySectionGroupId(long sectionGroupId);
}
