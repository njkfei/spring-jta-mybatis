package xyz.hollysys.spring.mybatis_super;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import xyz.hollysys.spring.mybatis_super.config.MybatisConfiguration;
import xyz.hollysys.spring.mybatis_super.model.Coupon;
import xyz.hollysys.spring.mybatis_super.model.User;
import xyz.hollysys.spring.mybatis_super.service.UserService;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String args[]){
        AbstractApplicationContext  context = new AnnotationConfigApplicationContext(MybatisConfiguration.class);
         
        UserService userService = (UserService) context.getBean("userService");
        
		User user=new User("njpmytabis","njpmytabis");
		Coupon coupon = new Coupon(user.getUsername(),"weixin",100);
		userService.save(user,coupon);
		
        context.close();
    }
}
