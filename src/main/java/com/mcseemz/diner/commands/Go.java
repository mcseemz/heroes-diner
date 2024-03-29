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
import java.util.Map;
import java.util.TreeMap;
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

    final String back = "<- Back";

    @SneakyThrows
    @ShellMethod(value = "Visit location")
    public void go() {

        Map<String, String> locs = Arrays.stream(state.getLocations())
                .filter(x -> x.isVisible() && !x.isPassed())    // || !x.isOnlyonce())
                .collect(Collectors.toMap(Location::getName, Location::getCode));
        locs.put(back, back);

        locs = new TreeMap<>(locs);

        ComponentFlow.ComponentFlowResult run = componentFlowBuilder.clone().reset()
                .withSingleItemSelector("location")
                .selectItems(locs)
                .and()
                .build().run();

        String myLocation = run.getContext().get("location");

        if (!myLocation.equals(back)) {
            Location location = Arrays.stream(state.getLocations()).filter(x -> x.getCode().equals(myLocation)).findFirst().orElseThrow();
            List<Hero> team = Arrays.stream(state.getRoster()).filter(Hero::isInTeam).collect(Collectors.toList());

            //run adventure
            Adventure adventure = new Adventure(team, location);
            List<BaseEvent> result = adventure.run();
            //compile results into text
            String report = compiler.compileReport(location, result);
            state.setLatestMessage(report);
            //update heroes with results when required
            state.updateGameState(location, result);
        }
        renderer.displayState();

        if (state.getState() == State.GAME_STATE.passed) {
            System.out.print(ansi().cursor(1, 1).eraseScreen(Ansi.Erase.FORWARD));

            for (String message : renderer.renderLatestMessage().split("\n")) {
                for (String str : renderer.splitString(message)) {
                    System.out.print(ansi().cursorToColumn(2).render(str).cursorDownLine());
                }
            }

            for (String the_beginning : state.getTexts().get("the_end_win")) {
                System.out.println(ansi().render(Renderer.postProcess(the_beginning)));
            }
        }
        if (state.getState() == State.GAME_STATE.lost) {
            System.out.print(ansi().cursor(1, 1).eraseScreen(Ansi.Erase.FORWARD));

            for (String message : renderer.renderLatestMessage().split("\n")) {
                for (String str : renderer.splitString(message)) {
                    System.out.print(ansi().cursorToColumn(2).render(str).cursorDownLine());
                }
            }

            for (String the_beginning : state.getTexts().get("the_end_loose")) {
                System.out.println(ansi().render(Renderer.postProcess(the_beginning)));
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
