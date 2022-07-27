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
import java.util.Optional;
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

    @ShellMethod(key = "team", value = "Team management - list")
    public void team(@ShellOption(defaultValue="list") String command) {
        StringBuilder builder = new StringBuilder();
        Arrays.stream(state.getRoster()).filter(Hero::isInTeam).forEachOrdered( hero -> hero.render(builder) );
        System.out.println(ansi().render(Renderer.postProcess(builder.toString())));
    }
    @ShellMethod(key = "team add", value = "Team management - add ")
    public void teamadd(@ShellOption(defaultValue="list") String command) {
        int teamSize = state.getTeam().size();

        if (teamSize < 5) {
            ComponentFlow.ComponentFlowResult run = componentFlowBuilder.clone().reset()
                    .withSingleItemSelector("hero")
                    .selectItems(Arrays.stream(state.getRoster())
                            .filter(hero -> !hero.isInTeam())
                            .filter(hero -> !hero.isOut())
                            .collect(Collectors.toMap(Hero::getName, Hero::getName)))
                    .and()
                    .build().run();

            String result = run.getContext().get("hero");
            for (Hero hero : state.getRoster()) {
                if (hero.getName().equals(result)) {
                    hero.setInTeam(true);
                }
            }
        }

        renderer.displayState();

        if (teamSize > 4) {
            System.out.println(ansi().fgRed().a("Team is at max now").reset());
        }
    }

    @ShellMethod(key = "team change", value = "Team management - replace ")
    public void teamchange(@ShellOption(defaultValue="list") String command) {

            ComponentFlow.ComponentFlowResult run = componentFlowBuilder.clone().reset()
                    .withSingleItemSelector("hero1")
                    .selectItems(state.getTeam().stream().collect(Collectors.toMap(Hero::getName, Hero::getName)))
                    .and()
                    .withSingleItemSelector("hero2")
                    .selectItems(Arrays.stream(state.getRoster())
                            .filter(hero -> !hero.isInTeam())
                            .filter(hero -> !hero.isOut())
                            .collect(Collectors.toMap(Hero::getName, Hero::getName)))
                    .and()
                    .build().run();

            String result1 = run.getContext().get("hero1");
            String result2 = run.getContext().get("hero2");
            for (Hero hero : state.getRoster()) {
                if (hero.getName().equals(result1)) {
                    hero.setInTeam(false);
                }
                if (hero.getName().equals(result2)) {
                    hero.setInTeam(true);
                }
            }

        renderer.displayState();
    }

    @ShellMethod(key = "team kick", value = "Team management - kick out person")
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

        ComponentFlow.ComponentFlowResult run = componentFlowBuilder.clone().reset()
                .withSingleItemSelector("hero")
                .selectItems(state.getTeam().stream().collect(Collectors.toMap(Hero::getName, Hero::getName)))
                .and()
                .withSingleItemSelector("skill")
                .selectItems(Arrays.stream(state.getSkills())
                        .collect(Collectors.toMap(x -> x, x -> x)))
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


    @ShellMethod(key = "team swap", value = "Team management - swap ")
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
