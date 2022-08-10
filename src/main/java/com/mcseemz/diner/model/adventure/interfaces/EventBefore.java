package com.mcseemz.diner.model.adventure.interfaces;

import com.mcseemz.diner.model.adventure.BaseEvent;

/**
 * happens in the beginning of an adventure
 */
public interface EventBefore extends EventProto {
    public BaseEvent run();

    int getProbability();
}
