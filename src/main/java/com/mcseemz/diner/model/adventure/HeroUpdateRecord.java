package com.mcseemz.diner.model.adventure;

import com.mcseemz.diner.model.Hero;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HeroUpdateRecord {
    BaseEvent.PropertyType type;
    String key;
    Object value;
    Hero hero;
}
