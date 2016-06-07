package edu.ucdavis.dss.ipa.services;

import org.springframework.validation.annotation.Validated;

import edu.ucdavis.dss.ipa.entities.TeachingCall;

@Validated
public interface TeachingCallService {

	public TeachingCall findOneById(Long id);

	public TeachingCall create(long scheduleId, TeachingCall teachingCallDTO);

	public TeachingCall findFirstByScheduleId(long id);

}
