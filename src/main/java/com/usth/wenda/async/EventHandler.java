package com.usth.wenda.async;

import com.usth.wenda.service.LikeService;

import java.util.List;

public interface EventHandler {

    void doHandler(EventModel model);

    List<EventType> getSupportEventTypes();
}
