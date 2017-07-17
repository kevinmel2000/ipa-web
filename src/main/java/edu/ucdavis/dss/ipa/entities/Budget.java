package edu.ucdavis.dss.ipa.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Budget is used to record information common to all budget scenarios.
 */

@SuppressWarnings("serial")
@Entity
@Table(name = "Budgets")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Budget {
    private long id;
    private Schedule schedule;
    private float taCost, readerCost, lecturerCost;
    private List<BudgetScenario> budgetScenarios = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", unique = true, nullable = false)
    @JsonProperty
    public long getId()
    {
        return this.id;
    }

    public void setId(long id)
    {
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

    public float getTaCost() {
        return taCost;
    }

    public void setTaCost(float taCost) {
        this.taCost = taCost;
    }

    public float getReaderCost() {
        return readerCost;
    }

    public void setReaderCost(float readerCost) {
        this.readerCost = readerCost;
    }

    public float getLecturerCost() {
        return lecturerCost;
    }

    public void setLecturerCost(float lecturerCost) {
        this.lecturerCost = lecturerCost;
    }

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "budget", cascade = {CascadeType.ALL})
    @JsonIgnore
    public List<BudgetScenario> getBudgetScenarios() {
        return budgetScenarios;
    }

    public void setBudgetScenarios(List<BudgetScenario> budgetScenarios) {
        this.budgetScenarios = budgetScenarios;
    }
}