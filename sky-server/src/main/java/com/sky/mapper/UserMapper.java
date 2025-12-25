package com.sky.mapper;


import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {


    /**
     * get user by openid
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByUserId(String openid);


    /**
     * insert user
     * @param user
     */
    void insert(User user);

    @Select("select * from user where id=#{userId}")
    User getById(Long userId);

    /**
     * dynamic query user by create time
     * @return
     */
    Integer countByMap(Map map);
}
