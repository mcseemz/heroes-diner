package com.mcseemz.diner;

import com.jakewharton.fliptables.FlipTable;
import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.Location;
import com.mcseemz.diner.model.Trial;
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
                        .addValue(renderLatestMessage()).addValue(renderStats())
                .build();

//        String output = new TableBuilder(model).addFullBorder(BorderStyle.fancy_light).build().render(80);

        String output = FlipTable.of(new String[] {"Locations", "Roster"}, new String[][]{
                {
                        postProcess(renderLocation()),
                        postProcess(renderRoster())
                },
                {
                        postProcess(renderLatestMessage()),
                        postProcess(renderStats())
                }
        });

//        output = postProcess(output);
        return ansi().eraseScreen().toString() + output;
    }

    public static String postProcess(String output) {
        output = output.replaceAll("\\*(.+?)\\*","@|bold  $1 |@");
        output = output.replaceAll("_(.+?)_","@|italic,underline  $1 |@");
        output = output.replaceAll("%(.+?)%","@|bold,red,underline  $1 |@");
        output = output.replaceAll("!(.+?)!","@|bold,red  $1 |@");
        return output;
    }

    private String renderLocation() {
        StringBuilder builder = new StringBuilder();
        for (Location location : state.getLocations()) {
            //check for hardest difficulty
            String hardest = "!";
            for (String locationTrial : location.getTrials()) {
                for (Trial stateTrial : state.getTrials()) {
                    if (locationTrial.equals(stateTrial.getCode())
                            && stateTrial.getDifficulty().compareTo(hardest) > 0) {
                        hardest = stateTrial.getDifficulty();
                    }
                }
            }
            builder.append(location.getName()).append(" ").append(hardest).append("\n");
            builder.append("  ").append(location.getDescription());
            builder.append("\n");
        }
        return builder.toString();
    }
    private String renderRoster() {
        StringBuilder builder = new StringBuilder();
        for (Hero hero : state.getRoster()) {
            if (hero.isInTeam()) builder.append("!*!");
            else if (hero.isOut()) builder.append("!-!");
            else builder.append("   ");

            hero.render(builder);
        }
        return builder.toString();
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
