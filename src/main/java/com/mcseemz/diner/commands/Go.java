package com.mcseemz.diner.commands;

import com.mcseemz.diner.Compiler;
import com.mcseemz.diner.Renderer;
import com.mcseemz.diner.State;
import com.mcseemz.diner.model.Adventure;
import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.Location;
import com.mcseemz.diner.model.adventure.BaseEvent;
import lombok.SneakyThrows;
import org.fusesource.jansi.Ansi;
import org.jline.reader.LineReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.Availability;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.fusesource.jansi.Ansi.ansi;

@ShellComponent
public class Go {

    @Autowired
    private State state;

    @Autowired
    private Renderer renderer;

    @Autowired
    private Compiler compiler;

    @Autowired
    private ComponentFlow.Builder componentFlowBuilder;

    @Autowired
    @Lazy
    LineReader lineReader;

    @SneakyThrows
    @ShellMethod(value = "Adventure start")
    public void go() {

        ComponentFlow.ComponentFlowResult run = componentFlowBuilder.clone().reset()
                .withSingleItemSelector("location")
                .selectItems(Arrays.stream(state.getLocations()).filter(x -> x.isVisible() && (!x.isPassed() || !x.isOnlyonce()) )
                        .collect(Collectors.toMap(Location::getName, Location::getCode)))
                .and()
                .withConfirmationInput("confirm")
                .defaultValue(true)
                .name("Are you sure you want to run this location with this team")
                .and()
                .build().run();

        Boolean confirmed = run.getContext().get("confirm");
        if (confirmed) {
            String myLocation = run.getContext().get("location");
            Location location = Arrays.stream(state.getLocations()).filter(x -> x.getCode().equals(myLocation)).findFirst().orElseThrow();
            List<Hero> team = Arrays.stream(state.getRoster()).filter(Hero::isInTeam).collect(Collectors.toList());

            //run adventure
            Adventure adventure = new Adventure(team, location);
            List<BaseEvent> result = adventure.run();
            //compile results into text
            String report = compiler.compileReport(result);
            state.setLatestMessage(report);
            //update heroes with results when required
            state.updateGameState(location, result);
        }

        renderer.displayState();
//        return ansi().cursorUp(37).eraseScreen(Ansi.Erase.FORWARD).render(renderer.renderState()).toString();

        if (state.getState() == State.GAME_STATE.passed) {
            System.out.print(ansi().cursor(1, 1).eraseScreen(Ansi.Erase.FORWARD));

            System.out.println(ansi().bg(Ansi.Color.GREEN).a("You finished the game! Congrats!").reset());
            for (String the_beginning : state.getTexts().get("the_end")) {
                System.out.println(ansi().render(the_beginning));

                int read = lineReader.getTerminal().reader().read();
                if (read == 'q') {
                    break;
                }
            }

        }
    }

    @ShellMethodAvailability
    public Availability goAvailability() {
        return state.getState() == State.GAME_STATE.waiting
                ? Availability.available()
                : Availability.unavailable("you need to be in the game first");
    }
}
