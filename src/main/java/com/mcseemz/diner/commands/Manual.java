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
        String sb = "  The game itself is a mix of Mafia and Wordle.\n" +
                "  This is a prototype, so it's in the text mode.\n" +
                "  There are bugs and missing parts (e.g. heroes gender pronouns.)\n" +
                "  And you have to enter commands with keyboard.\n\n" +
                "  The task of the game is to pass the toughest location, Dracula's Castle.\n" +
                "  For that you should have properly compiled team of 5, with 4 different adventurers skills, and one leader.\n" +
                "  The leader is a special type that rarely participates, but can empower others, or make a note of their behaviour.\n\n" +
                "  By visiting different locations you will get a report about team behaviour, skills and teamwork.\n" +
                "  Some information mapped to heroes and location automatically, other you should deduct.\n" +
                "  You may use team management commands to mark skills you suggest for team members.\n\n" +
                "  Each location consists of several trials, and optional teamwork event.\n" +
                "  Trials are mandatory, while winning teamwork will give you bonuses.\n" +
                "  Although there are also trials requiring enough teamwork to pass.\n\n" +
                "  Passing trial will make heroes more powerful.\n" +
                "  You can also use powerups from bonuses to assign more powers to a hero.\n" +
                "  Powered Leader can identify bad actors.\n\n" +
                "  The report you get at the end of visiting location, it might contain multiple events.\n" +
                "  Read it carefully, and check for the hints.\n" +
                "  You have limited number of turns to find your ideal team.\n\n" +
                "  In your team there are 10 heroes. 2 per each skill.\n" +
                "  One hero per skill always has teamwork of +1, other have 0, and couple even -1.\n" +
                "  Total teamwork you can see after passing a location, having bonus or not.\n\n" +
                "  The screen has 4 panels. Top left is the list of locations.\n" +
                "  Top right is your roster with marked team members.\n" +
                "  Bottom left is the latest report. And bottom right is the current stats.\n\n" +
                "  Check help command for a list of available commands.\n" +
                "  You may kick a person out if you are sure it's bad actor.\n" +
                "  And you have a single save slot to save you game in your working folder.";

        System.out.println(sb);
    }


}
