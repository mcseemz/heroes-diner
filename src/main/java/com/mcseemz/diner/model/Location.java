package com.mcseemz.diner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mcseemz.diner.State;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fusesource.jansi.Ansi;

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
    int teamwork;
    /** how they show teamwork. related to text resources */
    String teamtask;

    /** is this the end of the game */
    boolean target;

    /** we initiated trials to manage them easier */
    @JsonIgnore
    List<Trial> trialsLoaded;

    /** list of skills required to pass this location. Taken from trials */
    Set<SkillSuggestion> skillsKnown = new HashSet<>();

    public String getDifficulty(State state) {
        String hardest = "!";
        for (String locationTrial : getTrials()) {
            for (Trial stateTrial : state.getTrials()) {
                if (locationTrial.equals(stateTrial.getCode())
                        && stateTrial.getDifficulty().compareTo(hardest) > 0) {
                    hardest = stateTrial.getDifficulty();
                }
            }
        }
        return hardest;
    }

    public void render(StringBuilder builder, State state) {
        String difficulty = getDifficulty(state);

        builder.append(isPassed() ? ansi().fg(Ansi.Color.GREEN).a("+ ").reset() : "  ").append(getName())
                .append(" ").append(difficulty)
                .append(" : ").append(getSkillsKnown().stream().sorted().map(SkillSuggestion::toString).collect(Collectors.joining(" ")))
                .append("\n");
        builder.append("    ").append(getDescription());
        builder.append("\n");
    }

}
