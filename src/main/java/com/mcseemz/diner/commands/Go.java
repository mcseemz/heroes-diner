package com.mcseemz.diner.commands;

import com.mcseemz.diner.Renderer;
import com.mcseemz.diner.State;
import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.Location;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.fusesource.jansi.Ansi.ansi;

@ShellComponent
public class Go {

    @Autowired
    private State state;

    @Autowired
    private Renderer renderer;

    @Autowired
    private ComponentFlow.Builder componentFlowBuilder;

    @ShellMethod(value = "Adventure start")
    public String go() {

        ComponentFlow.ComponentFlowResult run = componentFlowBuilder.clone().reset()
                .withSingleItemSelector("location")
                .selectItems(Arrays.stream(state.getLocations())
                        .collect(Collectors.toMap(Location::getName, Location::getCode)))
                .and()
                .withConfirmationInput("confirm")
                .defaultValue(true)
                .name("Are you sure you want to run this location with this team")
                .and()
                .build().run();

        Boolean confirmed = run.getContext().get("confirm");
        if (confirmed) {
            String result = run.getContext().get("location");
            //todo run adventure
            //todo compile results into text
            //todo update heroes with results when required

        }

        return ansi().cursorUp(37).eraseScreen(Ansi.Erase.FORWARD).render(Renderer.postProcess(renderer.renderState())).toString();
    }

    @ShellMethodAvailability
    public Availability goAvailability() {
        return state.getState() == State.GAME_STATE.idle
                ? Availability.unavailable("you need to be in the game first")
                : Availability.available();
    }
}
