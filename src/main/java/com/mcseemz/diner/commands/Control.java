package com.mcseemz.diner.commands;

import com.mcseemz.diner.Compiler;
import com.mcseemz.diner.Renderer;
import com.mcseemz.diner.State;
import lombok.SneakyThrows;
import org.jline.reader.LineReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.Availability;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.io.IOException;

import static org.fusesource.jansi.Ansi.ansi;

@ShellComponent
public class Control {

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
    @ShellMethod(value = "Redraw screen")
    public void show() {

        renderer.displayState();
//        return ansi().cursorUp(37).eraseScreen(Ansi.Erase.FORWARD).render(renderer.renderState()).toString();

    }

    @ShellMethod(value = "Save game")
    @ShellMethodAvailability("goAvailability")
    public void save() throws IOException {
        state.save();
        renderer.displayState();
        System.out.println("Game saved");
    }

    @ShellMethod(value = "Load game")
    public void load() throws IOException {
        state.load();
        renderer.displayState();
        System.out.println(ansi().render("@|italic Game loaded |@").reset());
    }

    public Availability goAvailability() {
        return state.getState() == State.GAME_STATE.waiting
                ? Availability.available()
                : Availability.unavailable("you need to be in the game first");
    }
}
