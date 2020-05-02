package com.usth.wenda.dao;

import com.usth.wenda.model.Message;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface MessageDao {
    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " from_Id,to_Id,content,has_read,conversation_id,created_date ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert("insert into " + TABLE_NAME + "(" + INSERT_FIELDS +
            ") values (#{fromId},#{toId},#{content},#{hasRead},#{conversationId},#{createdDate})")
    int addMessage(Message message);

    @Select("select" + SELECT_FIELDS + " from " + TABLE_NAME +
            " where conversation_id=#{conversationId} order by created_date desc limit #{offset},#{limit}")
    List<Message> findConversationDetail(@Param("conversationId") String conversationId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    //select *, count(id) as id from (select * from message where from_id = userid or to_id = userid order by created_date desc) tt group by conversation_id order by created_date desc limit 0,10
    @Select("select " + INSERT_FIELDS + ", count(id) as id from (select * from " + TABLE_NAME +
            " where from_id=#{userId} or to_id=#{userId} order by created_date desc) tt group by conversation_id order by created_date desc limit #{offset},#{limit}")
    List<Message> findConversationList(@Param("userId") int userId,
                                       @Param("offset") int offset,
                                       @Param("limit") int limit);

    @Select("select count(id) from " + TABLE_NAME + " where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}")
    int findConversationUnreadCount(@Param("userId") int userId,@Param("conversationId") String conversationId);

    @Update("update " + TABLE_NAME + " set has_read=1 where to_id=#{userId} and conversation_id=#{conversationId}")
    int updateConersationHasRead(@Param("userId") int userId,@Param("conversationId") String conversationId);
}