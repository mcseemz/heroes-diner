package com.mcseemz.diner;

import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.Location;
import com.mcseemz.diner.model.adventure.BaseEvent;
import com.mcseemz.diner.model.adventure.ConflictEvent;
import com.mcseemz.diner.model.adventure.ConflictLeadersEvent;
import com.mcseemz.diner.model.adventure.HeroUpdateRecord;
import com.mcseemz.diner.model.adventure.LeaderTreatEvent;
import com.mcseemz.diner.model.adventure.SameskillEvent;
import com.mcseemz.diner.model.adventure.TeamworkEvent;
import com.mcseemz.diner.model.adventure.TrialEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * compile adventure report
 */
@Component
public class Compiler {
    @Autowired
    private State state;

    Random random = new Random();

    public String compileReport(Location location, List<BaseEvent> events) {

        StringBuilder sb = new StringBuilder();
        sb.append("_We went to ").append(location.getName()).append(":_");
        sb.append("\n");

        boolean isFirst = true;
        for (BaseEvent baseEvent : events) {
            if (baseEvent instanceof TrialEvent) {
                TrialEvent event = (TrialEvent) baseEvent;
                //check if it was attempted by hero, and if failed
                Hero hero = event.getHero();

                //we need 2 strings: outcome, and enemy
                //for outcome
                String outcome = isFirst ? "first_" : "";
                //for enemy
                String enemy = "";

                outcome += "trial_" + (event.isPassed() ? "succeed_" : "failed_");
                enemy += "trial_" + (event.isPassed() ? "succeed_" : "failed_");

                enemy += event.getTrial().getCode() + "_";

                outcome += hero == null ? "no_hero" : "with_hero";
                enemy += hero == null ? "no_hero" : "with_hero";

                String outcomeVal = getRandomLine(outcome);
                String enemyVal = getRandomLine(enemy);

                //post_process
                if (hero != null) {
                    outcomeVal = outcomeVal.replace("%hero%", "#" + hero.getName() + "#");
                    enemyVal = enemyVal.replace("%hero%", "#"+hero.getName()+"#");
                }

                sb.append(outcomeVal).append(" ").append(enemyVal);

                //trial failed with not enough teamwork
                if (hero != null && !event.isPassed() && event.isNoTeamwork()) {
                    String noTeamwork = getRandomLine("trial_failed_no_teamwork");
                    sb.append(" ").append(noTeamwork);
                }
                sb.append("\n");
            } else
            if (baseEvent instanceof TeamworkEvent) {
                TeamworkEvent event = (TeamworkEvent) baseEvent;
                String resource = "teamwork_" + (event.isPassed() ? "succeed_" : "failed_") + event.getLocation().getTeamwork().getTask();
                String resourceVal = getRandomLine(resource);

                //todo add placeholder replacements %hero%"
                sb.append(resourceVal).append("\n");

                if (event.isPassed()) {
                    boolean isFirstBonus = true;
                    for (String bonus : event.getLocation().getTeamwork().getBonus()) {
                        if (bonus.startsWith("map_")) {
                            resource = (isFirstBonus ? "first_" : "") + "teamwork_found_map";
                        } else {
                            resource = (isFirstBonus ? "first_" : "") + "teamwork_found_" + bonus;
                        }

                        String bonusVal = getRandomLine(resource);

                        if (bonus.startsWith("map_")) { //resolve map
                            String locname = bonus.split("_")[1];
                            Location bonus_location = Arrays.stream(state.getLocations()).filter(x -> x.getCode().equals(locname)).findFirst().orElseThrow();
                            bonusVal = bonusVal.replace("%location%", ">" + bonus_location.getName() + "<");
                        }

                        sb.append(bonusVal).append(" ");

                        isFirstBonus = false;
                    }
                }
                sb.append("\n");

                //add leader notes if event has a "leader" flag and probability (?) favors
                //all the data should be in the event, e.g. leader saw that hero had zero teamwork thus not participated
                //maybe even depends on the leader level? how does leaders level up?
                for (HeroUpdateRecord update : baseEvent.getLeaderNotes()) {
                    Hero hero = update.getHero();

                    switch (update.getType()) {
                        case bad_actor: {
                            resource = "leader_bad_teamwork";
                            if (hero != null) {
                                resource += "_hero";
                            }

                            String note = getRandomLine(resource).replace("%hero%", hero != null ? ("#" + hero.getName() + "#") : "");
                            sb.append("(leader): ").append(note).append("\n");
                            break;
                        }
                        default:
                            throw new IllegalStateException("Unexpected value: " + update.getType());
                    }
                }
            }
            else
            if (baseEvent instanceof ConflictEvent) {
                ConflictEvent event = (ConflictEvent) baseEvent;
                //check if it was attempted by hero, and if failed
                Hero good = event.getGoodActor();
                Hero bad = event.getBadActor();

                String resource = "event_conflict";
                String note = getRandomLine(resource)
                        .replace("%hero1%", "#" + good.getName() + "#")
                        .replace("%hero2%", "#" + bad.getName() + "#");
                sb.append(note).append("\n");
            }
            else
            if (baseEvent instanceof ConflictLeadersEvent) {
                ConflictLeadersEvent event = (ConflictLeadersEvent) baseEvent;
                //check if it was attempted by hero, and if failed
                Hero leader1 = event.getLeaders().get(0);
                Hero leader2 = event.getLeaders().get(1);

                String resource = "event_leader_conflict";
                String note = getRandomLine(resource)
                        .replace("%hero1%", "#" + leader1.getName() + "#")
                        .replace("%hero2%", "#" + leader2.getName() + "#");
                sb.append(note).append("\n");
            }
            else
            if (baseEvent instanceof SameskillEvent) {
                SameskillEvent event = (SameskillEvent) baseEvent;
                //check if it was attempted by hero, and if failed
                Hero hero1 = event.getHero1();
                Hero hero2 = event.getHero2();

                String monster = getRandomMonster(hero1.getSkill());
                String resource = "event_sameskill";
                String note = getRandomLine(resource)
                        .replace("%hero1%", "#" + hero1.getName() + "#")
                        .replace("%hero2%", "#" + hero2.getName() + "#")
                        .replace("%monster%", monster);
                sb.append(note).append("\n");
            }
            else
            if (baseEvent instanceof LeaderTreatEvent) {
                LeaderTreatEvent event = (LeaderTreatEvent) baseEvent;
                for (HeroUpdateRecord update : baseEvent.getLeaderNotes()) {
                    String resource = "---";
                    switch (update.getType()) {
                        case get_a_treat: {
                            resource = "leader_treat";

                            String note = getRandomLine(resource);
                            sb.append("(leader): ").append(note).append("\n");
                            break;
                        }
                        default:
                            throw new IllegalStateException("Unexpected value: " + update.getType());
                    }
                }

            }
            else
            if (baseEvent == null) {
                //do nothing
            } else {
                throw new RuntimeException("unexpected event type :" + baseEvent.getClass().getSimpleName());
            }
            isFirst = false;
        }

        return sb.toString();
    }

    /** get random line from resource */
    private String getRandomLine(String resource) {
        String[] bonuses = state.getTexts().get(resource);
        if (bonuses == null) {
            throw new RuntimeException("no resource found for :" + resource);
        }
        return bonuses[random.nextInt(bonuses.length)];
    }
    /** get random line from resource */

    private String getRandomMonster(String skill) {
        String[] bonuses = state.getSkills().get(skill);
        if (bonuses == null) {
            throw new RuntimeException("no monster found for :" + skill);
        }
        return bonuses[random.nextInt(bonuses.length)];
    }
}
