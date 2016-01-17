package xyz.hollysys.spring.mybatis_super.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import xyz.hollysys.spring.mybatis_super.dao.CouponDao;
import xyz.hollysys.spring.mybatis_super.dao.UserDao;
import xyz.hollysys.spring.mybatis_super.model.Coupon;
import xyz.hollysys.spring.mybatis_super.model.User;

@Service("userService")
public class UserService {
	
	@Autowired
	@Qualifier("userDao")
	private UserDao userDao;
	@Autowired
	@Qualifier("couponDao")
	private CouponDao couponDao;
	
	@Transactional
	public void save(User user,Coupon coupon){
		userDao.insertUser(user);
		couponDao.insertCoupon(coupon);
	}
}
