package com.usth.wenda.dao;

import com.usth.wenda.model.Comment;
import com.usth.wenda.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentDao {

    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = " user_id,content,created_date,entity_id,entity_type,status ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert("insert INTO " + TABLE_NAME + " (" + INSERT_FIELDS +
            ") VALUES (#{userId},#{content},#{createdDate},#{entityId},#{entityType},#{status}); ")
    int addComment(Comment comment);

    @Select("select " + SELECT_FIELDS + " from " + TABLE_NAME +
            " where entity_id=#{entityId} and entity_type=#{entityType} order by created_date desc")
    List<Comment> findCommentByEntity(@Param("entityId") int entityId,
                                      @Param("entityType") int entityType);

    @Select("select count(id) from " + TABLE_NAME + " where entity_id=#{entityId} and entity_type=#{entityType}")
    int getCommentCount(@Param("entityId") int entityId,
                        @Param("entityType") int entityType);

    @Update("update " + TABLE_NAME + " set status=#{status} where id=#{id}")
    int updateStatus(@Param("status") int status,@Param("id") int id);

    @Select("select " + SELECT_FIELDS + " from " + TABLE_NAME + " where id=#{id}")
    Comment findCommentById(int id);

    @Select({"select count(id) from ", TABLE_NAME, " where user_id=#{userId}"})
    int getUserCommentCount(int userId);
}
