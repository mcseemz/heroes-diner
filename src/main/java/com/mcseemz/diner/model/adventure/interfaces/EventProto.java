package com.mcseemz.diner.model.adventure.interfaces;

import com.mcseemz.diner.model.Hero;
import com.mcseemz.diner.model.adventure.BaseEvent;

import java.util.List;

public interface EventProto {
    public abstract BaseEvent getInitialized(List<Hero> team);
}
