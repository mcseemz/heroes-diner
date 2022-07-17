package com.mcseemz.diner.model.adventure;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HeroUpdateRecord {
    BaseEvent.PropertyType type;
    String key;
    Object value;
}
