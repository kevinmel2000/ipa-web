package edu.ucdavis.dss.ipa.services;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import edu.ucdavis.dss.ipa.entities.Term;

@Validated
public interface TermService {
	Term save(@NotNull @Valid Term term);

	Term getOneByTermCode(String termCode);

	Term findOrCreateByTermCode(String termCode);

	List<Term> findByYear(String year);
	
	Term updateOrCreate(Term term);

	List<Term> findByTermCodeInAndExistingEndDateAfterNow(Set<String> termCodes);

	List<Term> findByStartDateAfter(Date targetDate);

	Boolean isHistoricalByTermCode(String termCode);
}
