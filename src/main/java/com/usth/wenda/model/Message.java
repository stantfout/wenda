package com.usth.wenda.model;

import java.util.Date;

public class Message {
    private int id;//主键
    private int fromId;//发送者ID
    private int toId;//接收者ID
    private String content;//内容
    private Date createdDate;//创建时间
    private int hasRead;//是否已读
    private String conversationId;//会话ID

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getHasRead() {
        return hasRead;
    }

    public void setHasRead(int hasRead) {
        this.hasRead = hasRead;
    }

    public String getConversationId() {
        if(fromId < toId) {
            return String.format("%d_%d",fromId,toId);
        } else  {
            return String.format("%d_%d",toId,fromId);
        }
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}
