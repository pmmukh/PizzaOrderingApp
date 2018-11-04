package com.codesample.pizzaorder.entities;

import java.util.ArrayList;
import java.util.List;

public class InventoryUpdate {

	private List<Ingredient> bases = new ArrayList<Ingredient>();
	private List<Ingredient> cheeses = new ArrayList<Ingredient>();
	private List<Ingredient> sauces = new ArrayList<Ingredient>();
	private List<Ingredient> toppings = new ArrayList<Ingredient>();
	
	public List<Ingredient> getBases() {
		return bases;
	}
	public void setBases(List<Ingredient> bases) {
		this.bases = bases;
	}
	public List<Ingredient> getCheeses() {
		return cheeses;
	}
	public void setCheeses(List<Ingredient> cheeses) {
		this.cheeses = cheeses;
	}
	public List<Ingredient> getSauces() {
		return sauces;
	}
	public void setSauces(List<Ingredient> sauces) {
		this.sauces = sauces;
	}
	public List<Ingredient> getToppings() {
		return toppings;
	}
	public void setToppings(List<Ingredient> toppings) {
		this.toppings = toppings;
	}
	
}
