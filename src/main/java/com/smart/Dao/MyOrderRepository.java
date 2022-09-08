package com.smart.Dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.Entities.MyOrder;

public interface MyOrderRepository  extends JpaRepository<MyOrder,Long>{
	
	public MyOrder findByOrderId(String orderId);

}
