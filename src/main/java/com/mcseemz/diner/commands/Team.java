package com.mcseemz.diner.commands;

import com.mcseemz.diner.Renderer;
import com.mcseemz.diner.State;
import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.SkillSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.SelectItem;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.mcseemz.diner.model.SkillSuggestion.Certainty.unsure_no;
import static com.mcseemz.diner.model.SkillSuggestion.Certainty.unsure_yes;
import static org.fusesource.jansi.Ansi.ansi;

@ShellComponent
public class Team {

    @Autowired
    private State state;

    @Autowired
    private Renderer renderer;

    @Autowired
    private ComponentFlow.Builder componentFlowBuilder;

    final String back = "<- Back";
    final String all = "-> All";
    final String teamwork = "teamwork";

    @ShellMethod(key = "team", value = "Team management - list", group = "team")
    public void team(@ShellOption(defaultValue="list") String command) {
        StringBuilder builder = new StringBuilder();
        Arrays.stream(state.getRoster()).filter(Hero::isInTeam).forEachOrdered( hero -> hero.render(builder) );
        System.out.println(ansi().render(Renderer.postProcess(builder.toString())));
    }
//    @ShellMethod(key = "team add", value = "Team management - add ")
//    public void teamadd(@ShellOption(defaultValue="list") String command) {
//        int teamSize = state.getTeam().size();
//
//        if (teamSize < 5) {
//            ComponentFlow.ComponentFlowResult run = componentFlowBuilder.clone().reset()
//                    .withSingleItemSelector("hero")
//                    .selectItems(Arrays.stream(state.getRoster())
//                            .filter(hero -> !hero.isInTeam())
//                            .filter(hero -> !hero.isOut())
//                            .collect(Collectors.toMap(Hero::getName, Hero::getName)))
//                    .and()
//                    .build().run();
//
//            String result = run.getContext().get("hero");
//            for (Hero hero : state.getRoster()) {
//                if (hero.getName().equals(result)) {
//                    hero.setInTeam(true);
//                }
//            }
//        }
//
//        renderer.displayState();
//
//        if (teamSize > 4) {
//            System.out.println(ansi().fgRed().a("Team is at max now").reset());
//        }
//    }

    @ShellMethod(key = "kick", value = "Team management - kick out person forever", group = "team")
    public void teamkick(@ShellOption(defaultValue="list") String command) {

        Map<String, String> heroes = state.getTeam().stream().collect(Collectors.toMap(Hero::getName, Hero::getName));
        heroes.put(back, back);

        ComponentFlow.ComponentFlowResult run = componentFlowBuilder.clone().reset()
                .withSingleItemSelector("hero")
                .selectItems(heroes)
                .and()
                .build().run();

        String result = run.getContext().get("hero");

        if (!result.equals(back)) {
            boolean isKicked = false;
            for (Hero hero : state.getRoster()) {
                if (hero.getName().equals(result)) {
                    hero.setOut(true);
                    hero.setInTeam(false);
                    isKicked = true;
                }
            }
            //add another hero instead
            if (isKicked) for (Hero hero : state.getRoster()) {
                if (!hero.isInTeam() && !hero.isOut()) {
                    hero.setInTeam(true);
                    break;
                }
            }
        }

        renderer.displayState();
    }

    @ShellMethod(key = "skill", value = "Team management - suggest skill", group = "team")
    public void teamsuggest(@ShellOption(defaultValue="list") String command) {

        Map<String, String> skills = state.getSkills().keySet().stream()
                .collect(Collectors.toMap(x -> x, x -> x));
        skills.put(back, back);
        skills.put(teamwork, teamwork);
        skills = new TreeMap<>(skills);

        Map<String, String> heroes = state.getTeam().stream().collect(Collectors.toMap(Hero::getName, Hero::getName));
        heroes.put(all, all);

        ComponentFlow.ComponentFlowResult run = componentFlowBuilder.clone().reset()
                .withSingleItemSelector("hero")
                .selectItems(heroes)
                .max(10)
//                .next(x -> x.getResultItem().get().getName().equals(back) ? null : "skill")
                .and()
                .withSingleItemSelector("skill")
                .selectItems(skills)
                .max(10)
                .and()
                .build().run();

        String result1 = run.getContext().get("hero");
        String skill = run.getContext().get("skill");
        if (!skill.equals(back))
        for (Hero hero : state.getRoster()) {
            if (hero.getName().equals(result1) || (hero.isInTeam()) && result1.equals(all)) {
                hero.getSuggestedSkills().removeIf(x -> x.getCode().equals(skill));
                hero.getSuggestedSkills().add(SkillSuggestion.builder().code(skill).certainty(unsure_yes).build());
            }
        }

        renderer.displayState();
    }

    @ShellMethod(key = "noskill", value = "Team management - suggest skill", group = "team")
    public void teamnosuggest(@ShellOption(defaultValue="list") String command) {

        Map<String, String> skills = state.getSkills().keySet().stream()
                .collect(Collectors.toMap(x -> x, x -> x));
        skills.put(back, back);
        skills.put(teamwork, teamwork);
        skills = new TreeMap<>(skills);

        Map<String, String> heroes = state.getTeam().stream().collect(Collectors.toMap(Hero::getName, Hero::getName));
        heroes.put(all, all);


        ComponentFlow.ComponentFlowResult run = componentFlowBuilder.clone().reset()
                .withSingleItemSelector("hero")
                .selectItems(heroes)
                .max(10)
//                .next(x -> x.getResultItem().get().getName().equals(back) ? null : "skill")
                .and()
                .withSingleItemSelector("skill")
                .selectItems(skills)
                .max(10)
                .and()
                .build().run();

        String result1 = run.getContext().get("hero");
        String skill = run.getContext().get("skill");
        if (!skill.equals(back))
        for (Hero hero : state.getRoster()) {
            if (hero.getName().equals(result1) || (hero.isInTeam()) && result1.equals(all)) {
                hero.getSuggestedSkills().removeIf(x -> x.getCode().equals(skill));
                hero.getSuggestedSkills().add(SkillSuggestion.builder().code(skill).certainty(unsure_no).build());
            }
        }

        renderer.displayState();
    }


    @ShellMethod(key = "set", value = "Team management - adjust team", group = "team")
    public void teamedit(@ShellOption(defaultValue="list") String command) {

        List<String> result = new ArrayList<>();
        while (true) {
            ComponentFlow.ComponentFlowResult run = componentFlowBuilder.clone().reset()
                    .withMultiItemSelector("heroes")
                    .max(12)
                    .selectItems(Arrays.stream(state.getRoster())
                            .map(x -> new SelectItem() {
                                @Override
                                public String name() {
                                    return x.getName();
                                }

                                @Override
                                public String item() {
                                    return x.getName();
                                }

                                @Override
                                public boolean enabled() {
                                    return !x.isOut() && x.getDaysToRest() == 0;
                                }

                                @Override
                                public boolean selected() {
                                    return x.isInTeam();
                                }
                            })

                            .collect(Collectors.toList()))
                    .and()
                    .build().run();
            result = run.getContext().get("heroes");
            if (result.size() >5 || result.isEmpty()) {
                System.out.println(ansi().render("@|italic Invalid team size. Should not be empty, should not be more than 5 |@").newline());
            }
            else break;
        }
        List<Hero> team = state.getTeam();
        team.forEach(x -> x.setInTeam(false));

        for (Hero hero : state.getRoster()) {
            if (result.contains(hero.getName())) {
                hero.setInTeam(true);
            }
        }

        renderer.displayState();
    }


    @ShellMethod(key = "swap", value = "Team management - swap to random members", group = "team")
    public void teamswap(@ShellOption(defaultValue="list") String command) {
        List<Hero> roster = new ArrayList<>(Arrays.asList(state.getRoster()));
        List<Hero> team = state.getTeam();

        team.forEach(x -> x.setInTeam(false));

        roster.removeAll(team);
        roster.removeIf(Hero::isOut);

        Collections.shuffle(roster);
        roster.stream().limit(5).forEach(x -> x.setInTeam(true));

        renderer.displayState();
    }

    @ShellMethod(key = "powerup", value = "Team management - add more power to a person", group = "team")
    public void teampowerup(@ShellOption(defaultValue="list") String command) {

        Map<String, String> heroes = state.getTeam().stream().collect(Collectors.toMap(Hero::getName, Hero::getName));
        heroes.put(back, back);

        if (state.getPowerups()>0) {
            ComponentFlow.ComponentFlowResult run = componentFlowBuilder.clone().reset()
                    .withSingleItemSelector("hero")
                    .selectItems(heroes)
                    .and()
                    .build().run();

            String result = run.getContext().get("hero");
            for (Hero hero : state.getRoster()) {
                if (hero.getName().equals(result)) {
                    hero.setPower(hero.getPower() + "*");
                    state.setPowerups(state.getPowerups() - 1 );
                }
            }
            renderer.displayState();
        }
        else {
            renderer.displayState();
            System.out.println(ansi().render("@|italic You have not enough powerups |@").newline());
        }


    }

    @ShellMethodAvailability
    public Availability teamAvailability() {
        return state.getState() == State.GAME_STATE.waiting
                ? Availability.available()
                : Availability.unavailable("you need to be in the game first");
    }
}
