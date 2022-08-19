package com.mcseemz.diner.model.adventure;

import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.Location;
import com.mcseemz.diner.model.adventure.interfaces.EventAfter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * here we do the check on team teamwork and location requirements.
 * When succeed, we dd the power to everyone's skill
 */
@Builder
@Getter
@AllArgsConstructor
public class TeamworkEvent extends BaseEvent {

    Location location;
    List<Hero> team;
    int teamWork = 0;

    boolean isPassed = false;

    public TeamworkEvent() {
        type = Type.teamwork;
    }

    public TeamworkEvent run() {

        Hero badHero = null;
        Hero leaderHero = null;

        //leader adds to teamwork
        for (Hero hero : team) {
            teamWork += hero.getTeamWork();
            if (hero.getTeamWork()<1) badHero = hero;
            if (hero.getSkill().equals("leadership")) leaderHero = hero;
        }

        if (teamWork >= location.getTeamwork().getMin()) {
            isPassed = true;

            //update stats for powerups
            for (String bonus : location.getTeamwork().getBonus()) {
                if (bonus.equals("powerups")) {
                    for (Hero hero : team) {
                        heroUpdates.add(HeroUpdateRecord.builder()
                                .hero(hero).type(PropertyType.powerup).value("*").build());
                    }
                }
            }

//            alternatives for powerups
//            for (Hero hero : team) {
//                if (hero.isActed()) {
//                    heroUpdates.add(HeroUpdateRecord.builder()
//                            .hero(hero).type(PropertyType.powerup).value("*").build());
//                }
//            }
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
}
