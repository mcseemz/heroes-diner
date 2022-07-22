package com.mcseemz.diner.commands;

import com.mcseemz.diner.Renderer;
import com.mcseemz.diner.State;
import com.mcseemz.diner.model.Hero;
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
import java.util.Arrays;
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
                    .selectItems(Arrays.stream(state.getRoster()).filter(hero -> !hero.isInTeam())
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
                    .selectItems(Arrays.stream(state.getRoster()).filter(hero -> !hero.isInTeam())
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

    @ShellMethodAvailability
    public Availability teamAvailability() {
        return state.getState() == State.GAME_STATE.waiting
                ? Availability.available()
                : Availability.unavailable("you need to be in the game first");
    }
}
