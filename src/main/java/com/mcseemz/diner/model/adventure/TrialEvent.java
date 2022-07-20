package com.mcseemz.diner.model.adventure;

import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.Trial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Builder
@Getter
@AllArgsConstructor
public class TrialEvent extends BaseEvent {

    Trial trial;
    Hero hero;

    boolean isPassed;
    int needRest = 0;

    public TrialEvent() {
        type = Type.encounter;
    }

    public TrialEvent run() {
        //todo can pass one difficulty more but needs rest. Or rest is random?
        //or if the win is due to teamwork boost then rest is required

        if (hero != null && hero.getPower().compareTo(trial.getDifficulty()) >= 0) {
            isPassed = true;

            heroUpdates.compute(hero, (x, records) -> {
                records = records == null
                       ? new ArrayList<>()
                       : records;
                records.add(new HeroUpdateRecord(PropertyType.skill_suggest, "", trial.getSkill()));
                return records;
                }
            );
        }

        return this;
    }
}
