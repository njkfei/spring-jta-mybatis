package xyz.hollysys.spring.mybatis_super.model;

public class Coupon {

	private Long id;
	private String username;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	private String type;
	private int value;
	public Long getId() {
		return id;
	}
	
	
	
	public Coupon(String username, String type, int value) {
		super();
		this.username = username;
		this.type = type;
		this.value = value;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "Coupon [id=" + id + ", type=" + type + ", value=" + value + "]";
	}
	
	

}
