package com.mcseemz.diner.model.adventure;

import com.mcseemz.diner.model.Hero;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseEvent {

    Type type;
    boolean isLeaderOnly = false;

    @Getter
    Map<Hero, List<HeroUpdateRecord>> heroUpdates = new HashMap<>();

    enum Type {
        encounter,  //trial done
        stat_modifier, //someting happend that changed team or hero parameters
        heroes_interaction, //heroes talk to each other
        hero_out, //hero is out of team
        teamwork, //the team acts
    }

    public enum PropertyType {
        skill_suggest,
        skill_unsuggest,
        isOut,
        needRest,
        powerup
    }

}
