package com.mcseemz.diner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mcseemz.diner.State;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fusesource.jansi.Ansi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.fusesource.jansi.Ansi.ansi;

@Data
@NoArgsConstructor
public class Location {

    String name;
    String code;
    String description;
    String[] trials;
    /** trials and teamwork passed */
    boolean isPassed;
    /** how much teamwork is needed to unlock */
    Teamwork teamwork;

    /** is this the end of the game */
    boolean target;
    /** is this location visible on a map now */
    boolean visible;
    /** available to pass only once */
    boolean onlyonce;

    /** we initiated trials to manage them easier */
    @JsonIgnore
    List<Trial> trialsLoaded;

    /** list of skills required to pass this location. Taken from trials */
    Set<SkillSuggestion> skillsKnown = new HashSet<>();

    @JsonIgnore
    public String getDifficulty() {
        String hardest = "!";
        for (Trial trial : getTrialsLoaded()) {
            if (trial.getDifficulty().compareTo(hardest) > 0) {
                hardest = trial.getDifficulty();
            }
        }

        return hardest;
    }

    @JsonIgnore
    public Set<String> getSkillsRequired() {
        Set<String> skillsRequired = new HashSet<>();
        for (Trial trial : getTrialsLoaded()) {
            skillsRequired.add(trial.getSkill());
        }
        return skillsRequired;
    }

    public void render(StringBuilder builder, State state) {
        String difficulty = getDifficulty();

        builder.append(" ")
                .append(isPassed() ? "$" + getName() + "$" : " " + getName())
                .append(" ").append(difficulty)
                .append(getSkillsKnown().size() > 0 ? " : " : "").append(getSkillsKnown().stream().sorted().map(SkillSuggestion::toString).collect(Collectors.joining(" ")))
                .append("\n");
        builder.append("    ").append(getDescription());
        builder.append("\n");
    }

    @Data
    public static class Teamwork {
        /** is leader required for this teamwork */
        boolean leader;
        /** minimum teamwork to pass */
        int min;
        /** task for team work */
        String task;
        /** bonuses to get */
        String[] bonus;
    }

}
