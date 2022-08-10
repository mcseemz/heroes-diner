package com.mcseemz.diner.commands;

import com.mcseemz.diner.Renderer;
import com.mcseemz.diner.State;
import com.mcseemz.diner.model.Location;
import lombok.extern.slf4j.Slf4j;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.jline.reader.LineReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.Availability;
import org.springframework.shell.Shell;
import org.springframework.shell.command.CommandContext;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.SelectItem;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.fusesource.jansi.Ansi.*;

@Slf4j
@ShellComponent
public class NewGame {

    @Autowired
    private State state;

    @Autowired
    private Renderer renderer;

    @Autowired
    @Lazy
    LineReader lineReader;

    @Autowired
    private ComponentFlow.Builder componentFlowBuilder;

    @ShellMethod(value = "Start a new game")
    public void start(@ShellOption(defaultValue="World") String game) throws IOException {
        state.newGame();

        System.out.print(ansi().cursor(1, 1).eraseScreen(Ansi.Erase.FORWARD));
        int cnt = 0;
        for (String the_beginning : state.getTexts().get("the_beginning")) {
            System.out.println(ansi().render(the_beginning));

            if (cnt == 2) {
                System.out.println();
                ComponentFlow.ComponentFlowResult run = componentFlowBuilder.clone().reset()
                        .withSingleItemSelector("who_are_we")
                        .selectItems(Arrays.stream(state.getTexts().get("who_are_we"))
                                .collect(Collectors.toMap(x -> x, x -> x)))
                        .and()
                        .build().run();
                System.out.println();
            }
            else {
                int read = lineReader.getTerminal().reader().read();
                if (read == 'q') {
                    break;
                }
            }

            cnt++;
        }


        renderer.displayState();
    }

    public Availability startAvailability() {
        return state.getState() == State.GAME_STATE.editProfile
                ? Availability.unavailable("please stop editing operation first")
                : Availability.available();
    }
}
