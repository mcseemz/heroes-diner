package com.mcseemz.diner;

import com.jakewharton.fliptables.FlipTable;
import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.Location;
import com.mcseemz.diner.model.Trial;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;
import org.springframework.shell.table.TableModelBuilder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static org.fusesource.jansi.Ansi.ansi;

@Component
public class Renderer {
    @Autowired
    private State state;

    public String renderState() {
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

        return ansi().eraseScreen().toString() + output;
    }

    public void displayState() {
        int COL_1 = 2;
        int COL_2 = 90;
        int ROW_1 = 2;
        int ROW_2 = 26;

        System.out.print(ansi().cursor(1, 1).eraseScreen(Ansi.Erase.FORWARD));
        System.out.print(ansi().cursor(ROW_1,COL_1));
        for (String str : renderLocation().split("\n")) {
            System.out.print(ansi().cursorToColumn(COL_1).render(str).cursorDownLine());
        }
        System.out.print(ansi().cursor(ROW_1,COL_2));
        for (String str : renderRoster().split("\n")) {
            System.out.print(ansi().cursorToColumn(COL_2).render(str).cursorDownLine());
        }
        System.out.print(ansi().cursor(ROW_2,COL_1));
        for (String str : renderLatestMessage().split("\n")) {
            System.out.print(ansi().cursorToColumn(COL_1).render(str).cursorDownLine());
        }
        System.out.print(ansi().cursor(ROW_2,COL_2));
        for (String str : renderStats().split("\n")) {
            System.out.print(ansi().cursorToColumn(COL_2).render(str).cursorDownLine());
        }
    }

    public static String postProcess(String output) {
        output = output.replaceAll("\\*(.+?)\\*","@|bold  $1 |@");
        output = output.replaceAll("_(.+?)_","@|italic,underline  $1 |@");
        output = output.replaceAll("%(.+?)%","@|bold,red,underline  $1 |@");
        output = output.replaceAll("!(.+?)!","@|bold,red  $1 |@");
        return output;
    }

    public String renderLocation() {
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
            builder.append(location.getName()).append(" ").append(hardest).append(location.isPassed() ? " (passed)" : "").append("\n");
            builder.append("  ").append(location.getDescription());
            builder.append("\n");
        }
        return postProcess(builder.toString());
    }
    public String renderRoster() {
        StringBuilder builder = new StringBuilder();
        for (Hero hero : state.getRoster()) {
            if (hero.isInTeam()) builder.append("!>!");
            else if (hero.isOut()) builder.append("!-!");
            else builder.append("   ");

            hero.render(builder);
        }
        return postProcess(builder.toString());
    }
    public String renderLatestMessage() {
        return postProcess(state.getLatestMessage());
    }
    public String renderStats() {
        StringBuilder builder = new StringBuilder().append("Turn: ").append(state.getTurn()).append("\n")
                .append("Locations: ").append(Arrays.stream(state.getLocations()).filter(Location::isPassed).count()).append("\n");

        if (state.getLatestTeamwork() >= 0) {
            builder.append("Latest teamwork: ").append(state.getLatestTeamwork()).append("\n");
        }

        return postProcess(builder.toString());
    }

}
