package com.codesample.pizzaorder.db;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Base {
	
	@Id
	private Long id;
	
	private String name;
	private String size;
	private Integer quantity;
	private Integer price;
	
	@OneToMany(fetch = FetchType.LAZY,
            cascade =  CascadeType.ALL,
            mappedBy = "base")
    private List<Selection> selection = new ArrayList<Selection>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public List<Selection> getSelection() {
		return selection;
	}
	public void setSelection(List<Selection> selection) {
		this.selection = selection;
	}
	public Long getId() {
		return id;
	}
	
}
