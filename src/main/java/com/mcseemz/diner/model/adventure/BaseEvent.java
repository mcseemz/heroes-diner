package com.mcseemz.diner.model.adventure;

import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.adventure.interfaces.EventProto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@NoArgsConstructor
public class BaseEvent implements EventProto {

    Type type;
    boolean isLeaderOnly = false;

    @Getter
    List<Hero> team;

    @Getter
    List<HeroUpdateRecord> heroUpdates = new ArrayList<>();
    @Getter
    List<HeroUpdateRecord> leaderNotes = new ArrayList<>();

    @Override
    public BaseEvent getInitialized(List<Hero> team) {
        throw new RuntimeException("initializing BaseEvent forbidden");
    }

    enum Type {
        encounter,  //trial done
        stat_modifier, //someting happend that changed team or hero parameters
        heroes_interaction, //heroes talk to each other
        hero_out, //hero is out of team
        teamwork, //the team acts
        leadership, //some action from the leader
    }

    /** for hero update */
    public enum PropertyType {
        skill_suggest,
        skill_unsuggest,
        isOut,
        needRest,
        powerup,
        bad_actor,   //we have problem with the team
        get_a_treat,   //we had some motivation from leader
    }

    public int getProbability() {
        return 100;
    }

    public int getTeamwork() {
        int teamWork = 0;
        for (Hero hero : team) {
            teamWork += hero.getTeamWork();
        }
        return teamWork;
    }

    public Hero getLeader() {
        for (Hero hero : team) {
            if (hero.getSkill().equals("leadership")) return hero;
        }
        return null;
    }

    public Hero getSameSkill(Hero hero1) {
        for (Hero hero : team) {
            if (!hero1.equals(hero) && hero.getSkill().equals(hero1.getSkill())) return hero;
        }
        return null;
    }

    public Hero getBadTeamwork() {
        for (Hero hero : team) {
            if (hero.getTeamWork()<1) return hero;
        }
        return null;
    }
}
