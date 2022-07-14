package com.mcseemz.diner.commands;

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
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;
import org.springframework.shell.table.TableModelBuilder;

import java.util.Arrays;
import java.util.List;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

@ShellComponent
public class NewGame {

    @Autowired
    private ComponentFlow.Builder componentFlowBuilder;

    @ShellMethod(value = "Start a new game")
    public String start(@ShellOption(defaultValue="World") String game) {
        //todo initiate resources
        System.out.println(ansi().eraseScreen().bgGreen().fgBlack().a("New game: ").reset().a(game));

        TableModel model = new TableModelBuilder<String>().addRow().addValue("Here we have a map").addValue("here we have a roster")
                .addRow().addValue("here we have a latest report").addValue("here we have a stats")
                .build();

        System.out.println(
            new TableBuilder(model).addFullBorder(BorderStyle.fancy_light).build().render(80)
        );


        List<SelectItem> multi1SelectItems = Arrays.asList(SelectItem.of("key1", "value1"),
                SelectItem.of("key2", "value2"), SelectItem.of("key3", "value3"));

        ComponentFlow flow = componentFlowBuilder.clone().reset()
                .withMultiItemSelector("component multi")
                .name("Multi1")
                .selectItems(multi1SelectItems)
                .and().build();

        flow.run();

        return "zxcv";
    }

    public Availability startAvailability() {
        return Availability.available();
//                : Availability.unavailable("you are not connected");
    }
}
