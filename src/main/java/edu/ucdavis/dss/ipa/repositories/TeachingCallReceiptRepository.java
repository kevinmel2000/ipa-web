package edu.ucdavis.dss.ipa.repositories;

import org.springframework.data.repository.CrudRepository;

import edu.ucdavis.dss.ipa.entities.TeachingCallReceipt;

import java.util.List;

public interface TeachingCallReceiptRepository extends CrudRepository<TeachingCallReceipt, Long> {
    List<TeachingCallReceipt> findByScheduleIdAndSendEmailAndIsDone(Long scheduleId, Boolean sendEmail, Boolean isDone);
}
