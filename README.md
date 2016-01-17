## 项目简介
 基于spring jdbctemplate atomikos 构建本项目,旨在学习分布式事务管理。下列均有所涉及：
 * spring 提供框架管理功能。
 * jdbctemplate提供数据访问框架，jdbctemplate相比mytabis更灵活。更轻量。
 * 全部采用注解驱动
 * atomikos提供分布式事务处理框架

	本文以用户注册给送一张优惠券为例，进行说明。
	当用户注册时，向用户表接入用户数据。同时向优惠券表加入一条优惠券记录。用户表和优惠表分别在不同的数据库中。

 ##　关键依赖源
 ``` bash
		<!-- atomikos -->
		<dependency>
			<groupId>com.atomikos</groupId>
			<artifactId>transactions-jdbc</artifactId>
			<version>4.0.0M4</version>
		</dependency>

		<!-- javaee -->
		<dependency>
			<groupId>javax</groupId>
			<artifactId>javaee-api</artifactId>
			<version>7.0</version>
		</dependency>

		<dependency>
			<groupId>org.springframework.data</groupId>
			<artifactId>spring-data-jpa</artifactId>
			<version>1.8.0.RELEASE</version>
		</dependency>
 ```
 ## 关键代码
 ### CouponDao
 ``` bash
@Repository
public class CouponDao {

	@Resource(name="jdbcTemplateB")
	private JdbcTemplate jdbcTemplate;
	
	public void save(Coupon coupon){
		jdbcTemplate.update("insert into coupon(username,type,value) values(?,?,?)",coupon.getUsername(),coupon.getType(),coupon.getValue());
	}
}
 ```

### UserDao
``` bash
@Repository
public class UserDao {

	@Resource(name="jdbcTemplateA")
	private JdbcTemplate jdbcTemplate;
	
	public void save(User user){
		jdbcTemplate.update("insert into user2(username,password) values(?,?)",user.getUsername(),user.getPassword());
	}
}
```

### UserService
``` bash
@Service("userService")
public class UserService {
	
	@Autowired
	private UserDao userDao;
	@Autowired
	private CouponDao couponDao;
	
	@Transactional
	public void save(User user,Coupon coupon){
		userDao.save(user);
		couponDao.save(coupon);
	}
}
```

### java config配置
``` bash
package xyz.hollysys.spring.mybatis_super.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.transaction.SystemException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages ={  "xyz.hollysys.spring.mybatis_super.dao","xyz.hollysys.spring.mybatis_super.service"})
@PropertySource(value = { "classpath:jdbc.properties", "classpath:log4j.properties" })
public class JdbcConfiguration {
	static Logger logger = Logger.getLogger(JdbcConfiguration.class);

	// master database
	@Value("${jdbc.driverClassName:com.mysql.jdbc.Driver}")
	private String driverClassName;

	@Value("${jdbc.url:jdbc:mysql://localhost:3306/sanhao_test}")
	private String url;

	@Value("${jdbc.username:root}")
	private String username;

	@Value("${jdbc.password:root}")
	private String password;
	
	// slave database
	@Value("${jdbc2.driverClassName:com.mysql.jdbc.Driver}")
	private String driverClassName2;

	@Value("${jdbc2.url:jdbc:mysql://localhost:3306/test}")
	private String url2;

	@Value("${jdbc2.username:root}")
	private String username2;

	@Value("${jdbc2.password:root}")
	private String password2;

	
	public static final String MAPPERS_PACKAGE_NAME_1 = "xyz.hollysys.spring.mybatis_super.model";


    //You need this
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
       return new PropertySourcesPlaceholderConfigurer();
    }
	

	
	@Bean(name="master",initMethod = "init", destroyMethod = "close")
	public AtomikosDataSourceBean AtomikosDataSourceA(){
		AtomikosDataSourceBean bean =  new AtomikosDataSourceBean();
		bean.setXaDataSourceClassName(MysqlXADataSource.class.getCanonicalName());
		bean.setXaProperties(xaPropertiesA());
		bean.setUniqueResourceName("master");
		bean.setMinPoolSize(1);
		bean.setMaxPoolSize(3);
		bean.setMaxIdleTime(60);
		bean.setPoolSize(1);
		return bean;
		
	}
	
	@Bean(name="slave",initMethod = "init", destroyMethod = "close")
	public AtomikosDataSourceBean AtomikosDataSourceB(){
		AtomikosDataSourceBean bean =  new AtomikosDataSourceBean();
		bean.setXaDataSourceClassName(MysqlXADataSource.class.getCanonicalName());
		bean.setXaProperties(xaPropertiesB());
		bean.setUniqueResourceName("slave");
		bean.setMinPoolSize(1);
		bean.setMaxPoolSize(3);
		bean.setMaxIdleTime(60);
		bean.setPoolSize(1);
		return bean;
		
	}
	
	@Bean
	public Properties xaPropertiesA() {
		Properties props = new Properties();
		props.setProperty("URL", url);
		props.setProperty("user", username);
		props.setProperty("password", password);
		return props;
	}	
	
	@Bean
	public Properties xaPropertiesB() {
		Properties props = new Properties();
		props.setProperty("URL", url2);
		props.setProperty("user", username2);
		props.setProperty("password", password2);
		return props;
	}


	@Bean
	public Map<Object, Object> targetDataSourcesJta() {
		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("master", AtomikosDataSourceA());
		map.put("slave", AtomikosDataSourceB());
		
		return map;
	}
	
	@Bean(name="jdbcTemplateA")
	public JdbcTemplate jdbcTempalteA(){
		JdbcTemplate template = new JdbcTemplate();
		template.setDataSource(AtomikosDataSourceA());
		return template;
	}
	
	@Bean(name="jdbcTemplateB")
	public JdbcTemplate jdbcTempalteB(){
		JdbcTemplate template = new JdbcTemplate();
		template.setDataSource(AtomikosDataSourceB());
		return template;
	}
	
	@Bean
	public UserTransactionManager userTransactionManager(){
		UserTransactionManager userJta = new UserTransactionManager();
		userJta.setForceShutdown(true);
		
		return userJta;
	}
	
	@Bean
	public UserTransactionImp userTransactionImp(){
		UserTransactionImp userJtaImpl = new UserTransactionImp();
		try {
			userJtaImpl.setTransactionTimeout(300);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return userJtaImpl;
	}
	
	@Bean(name="transactionManager")
	public JtaTransactionManager jtaTransactionManager(){
		logger.info("transactionManager");
		JtaTransactionManager jta = new JtaTransactionManager();
		//jta.setTransactionManager(userTransactionManager());
		jta.setUserTransaction(userTransactionImp());
		return jta;
	}
	
}
```

###　测试代码
``` bash
public class App 
{
    public static void main(String args[]){
        AbstractApplicationContext  context = new AnnotationConfigApplicationContext(JdbcConfiguration.class);
         
        UserService userService = (UserService) context.getBean("userService");
        
		User user=new User("njp","njp");
		Coupon coupon = new Coupon(user.getUsername(),"weixin",100);
		userService.save(user,coupon);
		
        context.close();
    }
}
```
 




本项目没有xml配置文件，全部以注解的方式，进行配置注入。
#### 项目代码: [https://github.com/njkfei/spring-jta-jdbctemplate.git](https://github.com/njkfei/spring-jta-jdbctemplate.git)
#### 项目参考：[www.websystique.com](http://www.websystique.com)
#### 个人blog: [wiki.niejinkun.com](http://wiki.niejinkun.com)

