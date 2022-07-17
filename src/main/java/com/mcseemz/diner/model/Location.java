package com.mcseemz.diner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
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
    boolean isPassed;   //already passed

    /** we initiated trials to manage them easier */
    @JsonIgnore
    List<Trial> trialsLoaded;

}
