package edu.ucdavis.dss.ipa.services.jpa;

import edu.ucdavis.dss.ipa.entities.ActivityLog;
import edu.ucdavis.dss.ipa.entities.User;
import edu.ucdavis.dss.ipa.entities.validation.Loggable;
import edu.ucdavis.dss.ipa.repositories.ActivityLogRepository;
import edu.ucdavis.dss.ipa.services.ActivityLogService;
import edu.ucdavis.dss.ipa.services.ActivityLogTagService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.List;


@Service
public class JpaActivityLogService implements ActivityLogService {
    @Inject ActivityLogRepository activityLogRepository;
    @Inject ActivityLogTagService activityLogTagService;

    @Override
    public ActivityLog logEntry(User user, String message) {
        ActivityLog newLog = new ActivityLog();
        newLog.setMessage(message);
        newLog.setUser(user);

        return activityLogRepository.save(newLog);
    }

    @Override
    public void logEntry(User user, Loggable entity, String message) {
        // Save the log and get an ActivityLog with an id
        ActivityLog activityLog = logEntry(user, message);

        activityLogTagService.addActivityLogTag(activityLog, entity);
    }

    @Override
    public void logEntry(User user, List<Loggable> entity, String message) {
        ActivityLog activityLog = logEntry(user, message);

        for (Loggable e : entity) {
            activityLogTagService.addActivityLogTag(activityLog, e);
        }
    }

    @Override
    public List<ActivityLog> findByUser(User user) {
        return activityLogRepository.findByUser(user);
    }

    @Override
    public ActivityLog findById(long id) {
        return activityLogRepository.findById(id);
    }

    @Override
    public List<ActivityLog> findByTimestampAfter(Timestamp timestamp) {
        return activityLogRepository.findByTimestampGreaterThan(timestamp);
    }

    @Override
    public List<ActivityLog> findByTimestampBefore(Timestamp timestamp) {
        return activityLogRepository.findByTimestampLessThan(timestamp);
    }

    @Override
    public List<ActivityLog> findByTimestamp(Timestamp timestamp) {
        return activityLogRepository.findByTimestamp(timestamp);
    }
}
