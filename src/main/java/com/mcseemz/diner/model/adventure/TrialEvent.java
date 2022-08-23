package com.mcseemz.diner.model.adventure;

import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.Trial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class TrialEvent extends BaseEvent {

    Trial trial;
    Hero hero;

    List<Hero> team;
    boolean isPassed;
    int needRest = 0;

    boolean noTeamwork = false;
    public TrialEvent() {
        type = Type.encounter;
    }

    public TrialEvent run() {
        //todo can pass one difficulty more but needs rest. Or rest is random?
        //todo leader can share their power (all but one *)
        //or if the win is due to teamwork boost then rest is required
        int teamWork = 0;

        for (Hero hero : team) {
            teamWork += hero.getTeamWork();
        }

        if (hero != null) {
            heroUpdates.add(HeroUpdateRecord.builder()
                    .hero(hero).type(PropertyType.skill_suggest).value(trial.getSkill()).build());
        }

        if (teamWork < trial.getTeamwork()) {
            noTeamwork = true;
        }
        else
        if (hero != null && hero.getPower().compareTo(trial.getDifficulty()) >= 0) {
            isPassed = true;
        }

        return this;
    }

    @Override
    public BaseEvent getInitialized(List<Hero> team) {
        throw new RuntimeException("Invalid TrialEvent initialization. Should not happen");
    }
}
