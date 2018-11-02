package com.codesample.pizzaorder.entities;

import java.util.List;

public class OrderItem {

	private Long baseId;
	private Long cheeseId;
	private Long sauceId;
	private List<Long> toppingIds;
	public Long getBaseId() {
		return baseId;
	}
	public void setBaseId(Long baseId) {
		this.baseId = baseId;
	}
	public Long getCheeseId() {
		return cheeseId;
	}
	public void setCheeseId(Long cheeseId) {
		this.cheeseId = cheeseId;
	}
	public Long getSauceId() {
		return sauceId;
	}
	public void setSauceId(Long sauceId) {
		this.sauceId = sauceId;
	}
	public List<Long> getToppingIds() {
		return toppingIds;
	}
	public void setToppingIds(List<Long> toppingIds) {
		this.toppingIds = toppingIds;
	}
	
	
}
