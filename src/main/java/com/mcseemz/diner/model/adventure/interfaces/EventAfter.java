package com.mcseemz.diner.model.adventure.interfaces;

import com.mcseemz.diner.model.adventure.BaseEvent;
import com.mcseemz.diner.model.adventure.ConflictEvent;

/**
 * happens at the end of adventure
 */
public interface EventAfter extends EventProto {

    BaseEvent run();

    int getProbability();
}
