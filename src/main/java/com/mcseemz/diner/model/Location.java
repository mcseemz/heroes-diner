package com.mcseemz.diner.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Location {

    String name;
    String code;
    String description;
    String[] trials;
}
