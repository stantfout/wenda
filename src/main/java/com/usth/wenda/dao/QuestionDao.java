package com.usth.wenda.dao;

import com.usth.wenda.model.Question;
import com.usth.wenda.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionDao {

    String TABLE_NAME = " question ";
    String INSERT_FIELDS = " title,content,user_id,created_date,comment_count ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert("insert INTO " + TABLE_NAME + " (" + INSERT_FIELDS + ") VALUES (#{title},#{content},#{userId},#{createdDate},#{commentCount}); ")
    int addQuestion(Question question);

    List<Question> findLatestQuestion(@Param("userId") int userId,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);

    @Select("select " + SELECT_FIELDS + " from " + TABLE_NAME + " where id=#{id}")
    Question findById(int id);

    @Update("update" + TABLE_NAME + " set comment_count=#{commentCount} where id=#{id}")
    int updateCommentCount(@Param("id") int id,@Param("commentCount") int commentCount);
}
