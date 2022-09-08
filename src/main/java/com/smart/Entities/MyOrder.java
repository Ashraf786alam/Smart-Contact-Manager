package com.smart.Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="orders")
public class MyOrder {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private String orderId;
	
	private int amount;
	
	private String status;
	
	@ManyToOne
	private User user;
	
	private String paymentId;

	@Override
	public String toString() {
		return "MyOrder [id=" + id + ", orderId=" + orderId + ", amount=" + amount + ", status=" + status + ", user="
				+ user + ", paymentId=" + paymentId + "]";
	}

	public MyOrder() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MyOrder(long id, String orderId, int amount, String status, User user, String paymentId) {
		super();
		this.id = id;
		this.orderId = orderId;
		this.amount = amount;
		this.status = status;
		this.user = user;
		this.paymentId = paymentId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	

}
