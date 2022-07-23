package com.mcseemz.diner.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * skills and their probability
 */
@Builder
@Getter
@EqualsAndHashCode
public class SkillSuggestion implements Comparable<SkillSuggestion> {

    String code;

    @EqualsAndHashCode.Exclude
    Certainty certainty = Certainty.no_data;

    @Override
    public int compareTo(SkillSuggestion o) {
        return this.code.compareTo(o.getCode());
    }

    public enum Certainty {
        no_data,
        found,  //skill present
        not_found,  //skill not present
        unsure, // maybe present
    }

    @Override
    public String toString() {
        switch (certainty) {
            case no_data: return "." + code + ".";
            case found: return  "+" + code + "+";
            case not_found: return "-" + code + "-";
            case unsure: return "?" + code + "?";
        }

        throw new RuntimeException("Skill with unknown certainty: " + certainty);
    }
}
