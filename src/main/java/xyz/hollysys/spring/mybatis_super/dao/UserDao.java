package xyz.hollysys.spring.mybatis_super.dao;

import org.apache.ibatis.annotations.Insert;

import xyz.hollysys.spring.mybatis_super.model.User;

public interface UserDao {
	
	//@InsertProvider(type = SqlProvider.class, method = "insertUser")
	@Insert("insert into user2(username,password) values(#{username},#{password})")
	int insertUser(User user);
}
