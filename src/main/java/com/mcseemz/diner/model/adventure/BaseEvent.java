package com.mcseemz.diner.model.adventure;

import com.mcseemz.diner.model.Hero;

import java.util.List;
import java.util.Map;

public abstract class BaseEvent {

    Type type;
    boolean isLeaderOnly = false;

    Map<Hero, List<HeroUpdateRecord>> heroUpdates;

    enum Type {
        encounter,  //trial done
        stat_modifier, //someting happend that changed team or hero parameters
        heroes_interaction, //heroes talk to each other
        hero_out //hero is out of team
    }

    enum PropertyType {
        skill_suggest,
        skill_unsuggest,
        isOut,
        needRest,
    }

}
