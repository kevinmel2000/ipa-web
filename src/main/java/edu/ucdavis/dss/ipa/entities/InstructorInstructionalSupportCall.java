package edu.ucdavis.dss.ipa.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import edu.ucdavis.dss.ipa.api.deserializers.InstructorInstructionalSupportCallDeserializer;

@SuppressWarnings("serial")
@Entity
@Table(name = "InstructorInstructionalSupportCalls")
@JsonDeserialize(using = InstructorInstructionalSupportCallDeserializer.class)
public class InstructorInstructionalSupportCall implements Serializable {
    private long id;
    private Schedule schedule;
    private String message, termCode;
    private Date startDate, dueDate;
    private boolean sendEmails, AllowSubmissionAfterDueDate;
    private List<InstructorInstructionalSupportCallResponse> instructorInstructionalSupportCallResponses = new ArrayList<>();
    private List<InstructorInstructionalSupportPreference> instructorInstructionalSupportPreferences = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", unique = true, nullable = false)
    @JsonProperty
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ScheduleId", nullable = false)
    @NotNull
    @JsonIgnore
    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    @Column(name = "Message", nullable = true)
    @JsonProperty
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Column(name = "DueDate", nullable = false)
    @JsonProperty
    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @Column(name = "StartDate", nullable = false)
    @JsonProperty
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @JsonProperty
    @Column(nullable = false)
    public String getTermCode() {
        return termCode;
    }

    public void setTermCode(String termCode) {
        this.termCode = termCode;
    }

    @JsonProperty
    @Column(nullable = false)
    public boolean isSendEmails() {
        return sendEmails;
    }

    public void setSendEmails(boolean sendEmails) {
        this.sendEmails = sendEmails;
    }

    @JsonProperty
    @Column(nullable = false)
    public boolean isAllowSubmissionAfterDueDate() {
        return AllowSubmissionAfterDueDate;
    }

    public void setAllowSubmissionAfterDueDate(boolean allowSubmissionAfterDueDate) {
        AllowSubmissionAfterDueDate = allowSubmissionAfterDueDate;
    }

    @JsonProperty("scheduleId")
    @Transient
    public long getScheduleIdentification() {
        if(schedule != null) {
            return schedule.getId();
        } else {
            return 0;
        }
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "instructorInstructionalSupportCall")
    @JsonIgnore
    public List<InstructorInstructionalSupportCallResponse> getInstructorInstructionalSupportCallResponses() {
        return instructorInstructionalSupportCallResponses;
    }

    public void setInstructorInstructionalSupportCallResponses(List<InstructorInstructionalSupportCallResponse> instructorInstructionalSupportCallResponses) {
        this.instructorInstructionalSupportCallResponses = instructorInstructionalSupportCallResponses;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "instructorInstructionalSupportCall")
    @JsonIgnore
    public List<InstructorInstructionalSupportPreference> getInstructorInstructionalSupportPreferences() {
        return instructorInstructionalSupportPreferences;
    }

    public void setInstructorInstructionalSupportPreferences(List<InstructorInstructionalSupportPreference> instructorInstructionalSupportPreferences) {
        this.instructorInstructionalSupportPreferences = instructorInstructionalSupportPreferences;
    }
}
