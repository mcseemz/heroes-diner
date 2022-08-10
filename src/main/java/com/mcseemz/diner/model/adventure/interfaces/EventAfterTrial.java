package com.mcseemz.diner.model.adventure.interfaces;

import com.mcseemz.diner.model.adventure.BaseEvent;

public interface EventAfterTrial extends EventProto {
    public BaseEvent run();

    int getProbability();
}
