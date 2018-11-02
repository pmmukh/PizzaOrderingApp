package com.codesample.pizzaorder.entities;

import java.util.ArrayList;
import java.util.List;

public class NewToppings {

	List<Topping> newToppings = new ArrayList<Topping>();

	public List<Topping> getNewToppings() {
		return newToppings;
	}

	public void setNewToppings(List<Topping> newToppings) {
		this.newToppings = newToppings;
	}
	
}
