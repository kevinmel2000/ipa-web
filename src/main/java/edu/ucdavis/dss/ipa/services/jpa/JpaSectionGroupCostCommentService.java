package edu.ucdavis.dss.ipa.services.jpa;

import edu.ucdavis.dss.ipa.entities.*;
import edu.ucdavis.dss.ipa.repositories.LineItemCommentRepository;
import edu.ucdavis.dss.ipa.repositories.SectionGroupCostCommentRepository;
import edu.ucdavis.dss.ipa.services.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
public class JpaSectionGroupCostCommentService implements SectionGroupCostCommentService {
    @Inject SectionGroupCostCommentRepository sectionGroupCostCommentRepository;
    @Inject UserService userService;
    @Inject SectionGroupCostService sectionGroupCostService;

    @Override
    public SectionGroupCostComment create(SectionGroupCostComment sectionGroupCostCommentDTO) {
        SectionGroupCostComment sectionGroupCostComment = new SectionGroupCostComment();

        User user = userService.getOneById(sectionGroupCostCommentDTO.getUser().getId());
        SectionGroupCost sectionGroupCost = sectionGroupCostService.findById(sectionGroupCostCommentDTO.getSectionGroupCost().getId());

        sectionGroupCostComment.setSectionGroupCost(sectionGroupCost);
        sectionGroupCostComment.setUser(user);
        sectionGroupCostComment.setAuthorName(user.getDisplayName());
        sectionGroupCostComment.setComment(sectionGroupCostCommentDTO.getComment());

        sectionGroupCostComment = this.sectionGroupCostCommentRepository.save(sectionGroupCostComment);

        return sectionGroupCostComment;
    }

    @Override
    public List<SectionGroupCostComment> findBySectionGroupCosts(List<SectionGroupCost> sectionGroupCosts) {
        List<SectionGroupCostComment> sectionGroupCostComments = new ArrayList<>();

        for (SectionGroupCost sectionGroupCost : sectionGroupCosts) {
            sectionGroupCostComments.addAll(sectionGroupCost.getSectionGroupCostComments());
        }

        return sectionGroupCostComments;
    }
}
