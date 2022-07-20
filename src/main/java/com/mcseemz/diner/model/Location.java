package com.mcseemz.diner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Location {

    String name;
    String code;
    String description;
    String[] trials;
    /** trials and teamwork passed */
    boolean isPassed;
    /** how much teamwork is needed to unlock */
    int teamwork;
    /** how they show teamwork. related to text resources */
    String teamtask;

    /** we initiated trials to manage them easier */
    @JsonIgnore
    List<Trial> trialsLoaded;

}
