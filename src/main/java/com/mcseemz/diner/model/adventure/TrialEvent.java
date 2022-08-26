package com.mcseemz.diner.model.adventure;

import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.Trial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
@AllArgsConstructor
public class TrialEvent extends BaseEvent {

    Trial trial;
    Hero hero;

    /** if someone helped him to win the trial */
    Hero helper;

    boolean isPassed;
    int needRest = 0;

    boolean noTeamwork = false;
    public TrialEvent() {
        type = Type.encounter;
    }

    public TrialEvent run() {

        if (hero == null) {
            return this;
        }

        Hero leader = getLeader();
        Hero sameSkill = getSameSkill(hero);

        //todo leader can share their power (all but one *)
        //or if the win is due to teamwork boost then rest is required

        heroUpdates.add(HeroUpdateRecord.builder()
                .hero(hero).type(PropertyType.skill_suggest).value(trial.getSkill()).build());

        if (getTeamwork() < trial.getTeamwork()) {
            noTeamwork = true;
        }
        else
        if (hero.getPower().compareTo(trial.getDifficulty()) >= 0) {
            isPassed = true;
        }
        else
        if (trial.getDifficulty().length() - hero.getPower().length() == 1) {   //lack 1 power, check for helper
            if (leader != null && leader.getPower().length() > 1) {
                helper = leader;
                isPassed = true;
            }
            if (sameSkill != null) {
                helper = sameSkill;
                isPassed = true;
            }
        }

        return this;
    }

    @Override
    public BaseEvent getInitialized(List<Hero> team) {
        throw new RuntimeException("Invalid TrialEvent initialization. Should not happen");
    }
}
