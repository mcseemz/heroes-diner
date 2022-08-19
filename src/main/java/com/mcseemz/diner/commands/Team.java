package com.mcseemz.diner.commands;

import com.mcseemz.diner.Renderer;
import com.mcseemz.diner.State;
import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.SkillSuggestion;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.component.MultiItemSelector;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.ResultMode;
import org.springframework.shell.component.flow.SelectItem;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

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

    @ShellMethod(key = "team", value = "Team management - list")
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

    @ShellMethod(key = "team kick", value = "Team management - kick out person forever")
    public void teamkick(@ShellOption(defaultValue="list") String command) {

        ComponentFlow.ComponentFlowResult run = componentFlowBuilder.clone().reset()
                .withSingleItemSelector("hero")
                .selectItems(state.getTeam().stream().collect(Collectors.toMap(Hero::getName, Hero::getName)))
                .and()
                .build().run();

        String result = run.getContext().get("hero");
        for (Hero hero : state.getRoster()) {
            if (hero.getName().equals(result)) {
                hero.setOut(true);
                hero.setInTeam(false);
            }
        }

        renderer.displayState();
    }

    @ShellMethod(key = "team skill", value = "Team management - suggest skill")
    public void teamsuggest(@ShellOption(defaultValue="list") String command) {

        Map<String, String> skills = state.getSkills().keySet().stream()
                .collect(Collectors.toMap(x -> x, x -> x));
        skills.put(back, back);

        Map<String, String> heroes = state.getTeam().stream().collect(Collectors.toMap(Hero::getName, Hero::getName));

        skills = new TreeMap<>(skills);


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
        for (Hero hero : state.getRoster()) {
            if (hero.getName().equals(result1)) {
                SkillSuggestion skillFound = hero.getSuggestedSkills().stream()
                        .filter(x -> x.getCode().equals(skill)).findFirst().orElse(null);
                if (skillFound == null)
                    hero.getSuggestedSkills().add(SkillSuggestion.builder().code(skill).certainty(SkillSuggestion.Certainty.unsure).build());
            }
        }

        renderer.displayState();
    }


    @ShellMethod(key = "team set", value = "Team management - adjust team")
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


    @ShellMethod(key = "team swap", value = "Team management - swap to random members")
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


    @ShellMethodAvailability
    public Availability teamAvailability() {
        return state.getState() == State.GAME_STATE.waiting
                ? Availability.available()
                : Availability.unavailable("you need to be in the game first");
    }
}
