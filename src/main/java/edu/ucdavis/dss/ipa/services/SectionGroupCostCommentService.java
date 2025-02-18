package edu.ucdavis.dss.ipa.services;

import edu.ucdavis.dss.ipa.entities.LineItem;
import edu.ucdavis.dss.ipa.entities.SectionGroupCost;
import edu.ucdavis.dss.ipa.entities.SectionGroupCostComment;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface SectionGroupCostCommentService {
    SectionGroupCostComment create(SectionGroupCostComment sectionGroupCostCommentDTO);

    List<SectionGroupCostComment> findBySectionGroupCosts(List<SectionGroupCost> sectionGroupCosts);
}