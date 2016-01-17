package xyz.hollysys.spring.mybatis_super.dao;

import org.apache.ibatis.annotations.Insert;

import xyz.hollysys.spring.mybatis_super.model.Coupon;

public interface CouponDao {	
	//@InsertProvider(type = SqlProvider.class, method = "insertCoupon")
	@Insert("insert into coupon(username,type,value) values(#{username},#{type},#{value})")
	int insertCoupon(Coupon coupon);
}
