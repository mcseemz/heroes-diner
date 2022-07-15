package com.mcseemz.diner.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Trial {

    String code;
    String description;
    String skill;

    /** number of stars */
    String difficulty;

}
