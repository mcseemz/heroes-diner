package com.mcseemz.diner.model.adventure;

import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.Location;
import com.mcseemz.diner.model.Trial;
import com.mcseemz.diner.model.adventure.interfaces.EventAfter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * here we do the check on team teamwork and location requirements.
 * When succeed, we dd the power to everyone's skill
 */
@Builder
@Getter
@AllArgsConstructor
public class TeamworkEvent extends BaseEvent implements EventAfter {

    Location location;
    List<Hero> team;
    int teamWork = 0;

    boolean isPassed = false;

    public TeamworkEvent() {
        type = Type.teamwork;
    }

    public TeamworkEvent run() {

        //todo should leader add to teamwork?
        for (Hero hero : team) {
            teamWork += hero.getTeamWork();
        }

        if (teamWork >= location.getTeamwork().getMin()) {
            isPassed = true;

            //update stats for powerups
            for (String bonus : location.getTeamwork().getBonus()) {
                if (bonus.equals("powerups")) {
                    for (Hero hero : team) {
                        heroUpdates.compute(hero, (x, records) -> {
                                    records = records == null
                                            ? new ArrayList<>()
                                            : records;
                                    records.add(new HeroUpdateRecord(PropertyType.powerup, "", "*"));
                                    return records;
                                }
                        );
                    }
                }
            }
        }

        return this;
    }
}
