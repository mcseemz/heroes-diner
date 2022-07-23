package com.mcseemz.diner;

import com.jakewharton.fliptables.FlipTable;
import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.Location;
import com.mcseemz.diner.model.Trial;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.fusesource.jansi.Ansi.ansi;

@Component
public class Renderer {
    @Autowired
    private State state;

    private final int COL1_WIDTH = 85;


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

    public List<String> splitString(String string) {
        Pattern p = Pattern.compile("\\G\\s*(.{1,"+COL1_WIDTH+"})(?=\\s|$)", Pattern.DOTALL);
        Matcher m = p.matcher(string);

        List<String> result = new ArrayList<>();
        while (m.find())
            result.add(m.group(1));

        return result;
    }

    public void displayState() {
        int COL_1 = 2;
        int COL_2 = COL_1 + COL1_WIDTH;
        int ROW_1 = 2;
        int ROW_2 = 26;

        System.out.print(ansi().cursor(1, 1).eraseScreen(Ansi.Erase.FORWARD));
        System.out.print(ansi().cursor(ROW_1,COL_1));
        for (String str : renderLocation().split("\n")) {
            System.out.print(ansi().cursorToColumn(COL_1).render(postProcess(str)).cursorDownLine());
        }
        System.out.print(ansi().cursor(ROW_1,COL_2));
        for (String str : renderRoster().split("\n")) {
            System.out.print(ansi().cursorToColumn(COL_2).render(postProcess(str)).cursorDownLine());
        }

        System.out.print(ansi().cursor(ROW_2,COL_1));
        int ROW_2_HEIGHT_1 = 1;
        for (String message : renderLatestMessage().split("\n")) {
            for (String str : splitString(message)) {
                System.out.print(ansi().cursorToColumn(COL_1).render(str).cursorDownLine());
                ROW_2_HEIGHT_1++;
            }
        }

        System.out.print(ansi().cursor(ROW_2,COL_2));
        int ROW_2_HEIGHT_2 = 1;
        for (String message : renderStats().split("\n")) {
            for (String str : splitString(message)) {
                System.out.print(ansi().cursorToColumn(COL_2).render(str).cursorDownLine());
                ROW_2_HEIGHT_2++;
            }
        }

        int ROW_2_HEIGHT = Math.max(ROW_2_HEIGHT_1, ROW_2_HEIGHT_2);

        System.out.print(ansi().cursor(ROW_2,COL_1).cursorDownLine(ROW_2_HEIGHT + 1));
    }

    public static String postProcess(String output) {
        output = output.replaceAll("\\*(.+?)\\*","@|bold  $1 |@");
        output = output.replaceAll("_(.+?)_","@|italic,underline  $1 |@");
        output = output.replaceAll("%(.+?)%","@|bold,red,underline  $1 |@");
        output = output.replaceAll("!(.+?)!","@|bold,red  $1 |@");

        //skill markup
        output = output.replaceAll("\\+(\\S+?)\\+","@|green  +$1 |@");
        output = output.replaceAll("-(\\S+?)-","@|black,faint -$1 |@");
        output = output.replaceAll("\\?(\\S+?)\\?","@|cyan  ?$1 |@");
        output = output.replaceAll("\\.(\\S+?)\\.","@|red  .$1 |@");

        return output;
    }

    public String renderLocation() {
        StringBuilder builder = new StringBuilder();
        for (Location location : state.getLocations()) {
            location.render(builder, state);
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
