package com.mcseemz.diner.model.adventure;

import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * here we do the check on team teamwork and location requirements.
 * When succeed, we dd the power to everyone's skill
 */
@SuperBuilder
@Getter
@AllArgsConstructor
public class TeamworkEvent extends BaseEvent {

    Location location;
    int teamWork = 0;

    boolean isPassed = false;

    public TeamworkEvent() {
        type = Type.teamwork;
    }

    public TeamworkEvent run() {

        Hero badHero = getBadTeamwork();
        Hero leaderHero = getLeader();

        location.setPassed(true);    //mark as passed anyway

        teamWork = getTeamwork();

        if (teamWork >= location.getTeamwork().getMin()) {
            isPassed = true;

            //update stats for powerups
            for (String bonus : location.getTeamwork().getBonus()) {
                if (bonus.equals("powerups")) {
                    heroUpdates.add(HeroUpdateRecord.builder()
                            .hero(null).type(PropertyType.powerup).value(1).build());
                }
            }
        }

        //anyway powerups for participated heroes
        for (Hero hero : team) {
            if (hero.isActed()) {
                heroUpdates.add(HeroUpdateRecord.builder()
                        .hero(hero).type(PropertyType.powerup).value(1).build());
            }
        }

        //leader note
        //todo should the leader report on himself?
        if (leaderHero != null && badHero != null) {
            //weak leader cannot identify bad actor
            switch (leaderHero.getPower().length()) {
                case 1:
                case 2:  leaderNotes.add(HeroUpdateRecord.builder().type(PropertyType.bad_actor).build());
                        break;
                default: leaderNotes.add(HeroUpdateRecord.builder().type(PropertyType.bad_actor).hero(badHero).build());
                        break;
            }
        }

        return this;
    }

    @Override
    public BaseEvent getInitialized(List<Hero> team) {
        throw new RuntimeException("Invalid TeamworkEvent initialization. Should not happen");
    }
}
