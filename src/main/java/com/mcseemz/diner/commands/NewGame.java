package com.mcseemz.diner.commands;

import com.mcseemz.diner.Renderer;
import com.mcseemz.diner.State;
import lombok.extern.slf4j.Slf4j;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.Shell;
import org.springframework.shell.command.CommandContext;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.SelectItem;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;

import static org.fusesource.jansi.Ansi.*;

@Slf4j
@ShellComponent
public class NewGame {

    @Autowired
    private State state;

    @Autowired
    private Renderer renderer;

    @Autowired
    private ComponentFlow.Builder componentFlowBuilder;

    @ShellMethod(value = "Start a new game")
    public void start(@ShellOption(defaultValue="World") String game) throws IOException {
        //todo initiate resources
        System.out.println(ansi().eraseScreen().bgGreen().fgBlack().a("New game: ").reset().a(game));

        log.debug("asdf!");
        state.newGame();

//        return ansi().render(renderer.renderState()).toString();

        renderer.displayState();
    }

    public Availability startAvailability() {
        return state.getState() == State.GAME_STATE.editProfile
                ? Availability.unavailable("please stop editing operation first")
                : Availability.available();
    }
}
