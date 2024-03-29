package com.mcseemz.diner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hero {

    String name;
    String skill;

    /** number of stars */
    String power;

    /** currently in the team */
    boolean inTeam;

    /** how many days to rest, if required */
    int daysToRest;

    /** not vailable anymore */
    boolean isOut;

    /** team player, -1. 0 or 1 */
    int teamWork;

    /** what player thinks of skills */
    @Getter
    Set<SkillSuggestion> suggestedSkills = new HashSet<>();

    /** acted in this adventure already */
    @JsonIgnore
    boolean isActed;

    public void render(StringBuilder builder) {
        if (isOut()) {
            builder.append("!!(out)!! ");
        }

        builder.append(this.getName()).append(" ").append(this.power);
        if (this.getDaysToRest() > 0) builder.append(" (rest: !!").append(this.getDaysToRest()).append("!!)");

        builder.append(getSuggestedSkills().size() > 0 ? " : " : "");
        for (SkillSuggestion suggestedSkill : this.getSuggestedSkills()) {
            builder.append(suggestedSkill).append(" ");
        }
        builder.append("\n");
    }

}
