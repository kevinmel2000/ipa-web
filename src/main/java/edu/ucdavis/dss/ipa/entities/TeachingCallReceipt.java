package edu.ucdavis.dss.ipa.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import edu.ucdavis.dss.ipa.web.views.TeachingCallReceiptViews;

/**
 * @author okadri
 * Stores instructor's notification time-stamps for a given TeachingCall 
 * It also has a flag to indicate whether the instructor is done inputing
 * preferences for the year
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "TeachingCallReceipts")
public class TeachingCallReceipt implements Serializable {
	private long id;

	private Instructor instructor;
	private Boolean isDone = false;
	private Date notifiedAt, warnedAt;
	private TeachingCall teachingCall;
	private String comment;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TeachingCallReceiptId", unique = true, nullable = false)
	@JsonView(TeachingCallReceiptViews.Detailed.class)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Instructors_InstructorId", nullable = false)
	@NotNull
	public Instructor getInstructor() {
		return instructor;
	}

	public void setInstructor(Instructor instructor) {
		this.instructor = instructor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TeachingCalls_TeachingCallId", nullable = false)
	@NotNull
	@JsonIgnore
	public TeachingCall getTeachingCall() {
		return teachingCall;
	}

	public void setTeachingCall(TeachingCall teachingCall) {
		this.teachingCall = teachingCall;
	}

	@Column(name = "isDone", nullable = false)
	@JsonView(TeachingCallReceiptViews.Detailed.class)
	public Boolean getIsDone() {
		return isDone;
	}

	public void setIsDone(Boolean isDone) {
		this.isDone = isDone;
	}

	public Date getNotifiedAt() {
		return notifiedAt;
	}

	public void setNotifiedAt(Date notifiedAt) {
		this.notifiedAt = notifiedAt;
	}

	public Date getWarnedAt() {
		return warnedAt;
	}

	public void setWarnedAt(Date warnedAt) {
		this.warnedAt = warnedAt;
	}

	@Column(name = "Comment", nullable = true)
	@JsonView(TeachingCallReceiptViews.Detailed.class)
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
