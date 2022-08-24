package com.mcseemz.diner.commands;


import lombok.SneakyThrows;
import org.fusesource.jansi.Ansi;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import static org.fusesource.jansi.Ansi.ansi;

@ShellComponent
public class Manual {

    @SneakyThrows
    @ShellMethod(value = "Game manual and hints")
    public void manual() {
        System.out.print(ansi().cursor(1, 1).eraseScreen(Ansi.Erase.FORWARD));
        StringBuilder sb = new StringBuilder();
        sb
                .append("  The game itself is a mix of Mafia and Wordle.\n")
                .append("  This is a prototype, so it's in the text mode.\n")
                .append("  You have to enter commands with keyboard.\n\n")

                .append("  The task of the game is to pass the toughest location, Dracula's Castle.\n")
                .append("  For that you should have properly compiled team of 5, with 4 different adventurers skills, and one leader.\n")
                .append("  The leader is a special type that rarely participates, but can empower others, or make a note of their behaviour.\n\n")

                .append("  By visiting different locations you will get a report about team behaviour, skills and teamwork.\n")
                .append("  Some information mapped to heroes and location automatically, other you should deduct.\n")
                .append("  You may use team management commands to mark skills you suggest for team members.\n\n")

                .append("  Each location consists of several trials, and optional teamwork event.\n")
                .append("  Trials are mandatory, while winning teamwork will give you bonuses.\n")
                .append("  Although there are also trials requiring enough teamwork to pass.\n\n")

                .append("  Passing trial will make heroes more powerful.\n")
                .append("  You can also powerups from bonuses to assign more powers to a hero.\n")
                .append("  Powered Leader can identify bad actors.\n\n")

                .append("  The report you get at the end of visiting location, it might contain multiple events.\n")
                .append("  Read it carefully, and check for the hints.\n")
                .append("  You have limited number of turns to find your ideal team.\n\n")

                .append("  In your team there are 10 heroes. 2 per each skill.\n")
                .append("  One hero per skill always has teamwork of +1, other have 0, and couple even -1.\n")
                .append("  Total teamwork you can see after passing a location, having bonus or not.\n\n")

                .append("  The screen has 4 panels. Top left is the list of locations.\n")
                .append("  Top right is your roster with marked team members.\n")
                .append("  Bottom left is the latest report. And bottom right is the current stats.\n\n")

                .append("  Check help command for a list of available commands.\n")
                .append("  You may kick a person out if you are sure it's bad actor.\n")
                .append("  And you have a single save slot to save you game in your working folder.");

        System.out.println(sb);
    }


}
