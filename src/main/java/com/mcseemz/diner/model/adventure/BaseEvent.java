package com.mcseemz.diner.model.adventure;

import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.adventure.interfaces.EventProto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseEvent implements EventProto {

    Type type;
    boolean isLeaderOnly = false;

    @Getter
    List<HeroUpdateRecord> heroUpdates = new ArrayList<>();
    @Getter
    List<HeroUpdateRecord> leaderNotes = new ArrayList<>();

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

}
