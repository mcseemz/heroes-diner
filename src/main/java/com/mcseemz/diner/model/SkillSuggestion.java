package com.mcseemz.diner.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * skills and their probability
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
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
        unsure_yes, // maybe present
        unsure_no, // maybe not present
    }

    @Override
    public String toString() {
        switch (certainty) {
            case no_data: return "." + code + ".";
            case found: return  "+" + code + "+";
            case not_found: return "-" + code + "-";
            case unsure_yes: return "?+" + code + "?";
            case unsure_no: return "?-" + code + "?";
        }

        throw new RuntimeException("Skill with unknown certainty: " + certainty);
    }
}
