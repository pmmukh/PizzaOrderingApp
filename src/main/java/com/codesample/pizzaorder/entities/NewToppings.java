package com.codesample.pizzaorder.entities;

import java.util.ArrayList;
import java.util.List;

public class NewToppings {

	List<Top> newToppings = new ArrayList<Top>();

	public List<Top> getNewToppings() {
		return newToppings;
	}

	public void setNewToppings(List<Top> newToppings) {
		this.newToppings = newToppings;
	}
	
}
