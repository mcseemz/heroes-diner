package com.mcseemz.diner.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    List<String> suggestedSkills;

    public void render(StringBuilder builder) {
        builder.append(this.getName());
        if (this.getDaysToRest() > 0) builder.append(" (rest: !").append(this.getDaysToRest()).append("!)");
        builder.append("\n    ");

        for (String suggestedSkill : this.getSuggestedSkills()) {
            builder.append(suggestedSkill).append("? ");
        }
        builder.append("\n");
    }

}
