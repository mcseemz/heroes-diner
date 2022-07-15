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
    public String teamadd(@ShellOption(defaultValue="list") String command) {

        ComponentFlow.ComponentFlowResult run = componentFlowBuilder.clone().reset()
                .withSingleItemSelector("hero")
                .selectItems(Arrays.stream(state.getRoster()).filter(hero -> !hero.isInTeam())
                        .collect(Collectors.toMap(Hero::getName, Hero::getName)))

//                .withMultiItemSelector("heroes")
//                .selectItems(Arrays.stream(state.getRoster()).map(hero -> SelectItem.of(hero.getName(), hero.getName(), !hero.isOut())).collect(Collectors.toList()))
//                .name("Select team members")
//                .max(5)
                .and()
                .build().run();

        String result = run.getContext().get("hero");
        for (Hero hero : state.getRoster()) {
            if (hero.getName().equals(result)) {
                hero.setInTeam(true);
            }
        }

        return ansi().cursorUp(37).eraseScreen(Ansi.Erase.FORWARD).render(Renderer.postProcess(renderer.renderState())).toString();
    }

    @ShellMethodAvailability
    public Availability teamAvailability() {
        return state.getState() == State.GAME_STATE.idle
                ? Availability.unavailable("you need to be in the game first")
                : Availability.available();
    }
}
