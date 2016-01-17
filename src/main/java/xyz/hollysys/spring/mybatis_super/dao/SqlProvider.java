package xyz.hollysys.spring.mybatis_super.dao;

import org.apache.ibatis.jdbc.SQL;
import org.apache.log4j.Logger;

import xyz.hollysys.spring.mybatis_super.model.Coupon;
import xyz.hollysys.spring.mybatis_super.model.User;

public class SqlProvider {
	static Logger logger = Logger.getLogger(SqlProvider.class);

	
	public String insertUser(final User user){
		String sql =  new SQL() {
            {
                INSERT_INTO("user2");

                if (user.getUsername() != null) {
                	VALUES("username","#{username}");
                }
                if (user.getPassword() != null) {
                	VALUES("password","#{password}");
                }
            }
        }.toString();
		
        logger.info(sql);
        
		return sql;
	}
	
	public String insertCoupon(final Coupon coupon){
		String sql =  new SQL() {
            {
                INSERT_INTO("coupon");

                if (coupon.getUsername() != null) {
                	VALUES("username","#{username}");
                }
                if (coupon.getType() != null) {
                	VALUES("type","#{type}");
                }
                if (coupon.getValue() != 0) {
                	VALUES("value","#{value}");
                }
            }
        }.toString();
		
        logger.info(sql);
        
		return sql;
	}
}
