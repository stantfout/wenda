package com.usth.wenda.async.handler;

import com.usth.wenda.async.EventHandler;
import com.usth.wenda.async.EventModel;
import com.usth.wenda.async.EventType;
import com.usth.wenda.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AddQuestionHandler implements EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(AddQuestionHandler.class);

    @Autowired
    SearchService searchService;

    @Override
    public void doHandler(EventModel model) {
        try {
            searchService.indexQuestion(model.getEntityId(),
                    model.getExt("questionTitle"), model.getExt("content"));
        } catch (Exception e) {
            logger.error("增加题目索引失败");
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Collections.singletonList(EventType.ADD_QUESTION);
    }
}
