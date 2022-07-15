package com.mcseemz.diner;

import org.fusesource.jansi.Ansi;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;
import org.springframework.shell.table.TableModelBuilder;
import org.springframework.stereotype.Component;

import static org.fusesource.jansi.Ansi.ansi;

@Component
public class Renderer {
    @Autowired
    private State state;

    public String renderState() {
                TableModel model = new TableModelBuilder<String>().addRow()
                        .addValue(renderLocation()).addValue(renderRoster())
                .addRow()
                        .addValue(state.getLatestMessage()).addValue(renderStats())
                .build();

        String output = new TableBuilder(model).addFullBorder(BorderStyle.fancy_light).build().render(80);
        output = output.replaceAll("\\*(.*?)\\*","@|bold  $1 |@");
        output = output.replaceAll("_(.*?)_","@|italic,underline  $1 |@");
        return ansi().eraseScreen().toString() + output;
    }

    private String renderLocation() {
        return "Here we have a map";
    }
    private String renderRoster() {
        return "here we have a roster";
    }
    private String renderLatestMessage() {
        return state.getLatestMessage();
    }
    private String renderStats() {
        StringBuilder builder = new StringBuilder().append("Turn: ").append(state.getTurn()).append("\n")
                .append("Locations: ").append(0);


        return builder.toString();
    }

}
