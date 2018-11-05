package com.codesample.pizzaorder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codesample.pizzaorder.entities.ConfirmItem;
import com.codesample.pizzaorder.entities.InventoryUpdate;
import com.codesample.pizzaorder.entities.NewToppings;
import com.codesample.pizzaorder.entities.OrderItem;
import com.codesample.pizzaorder.exceptions.InvalidRequestException;
import com.codesample.pizzaorder.service.OrderService;

@RestController
public class OrderController {

	@Autowired
	OrderService orderService;
	
	@RequestMapping(value="/order/addPizza/{uid}", method = RequestMethod.POST)
	public ConfirmItem addItem(@PathVariable("uid") Long userId, @RequestBody OrderItem item) {
		try {
			ConfirmItem confirmItem = orderService.addItem(item, userId);
			return confirmItem;
		} catch(InvalidRequestException ire) {
			throw ire;
		}
	}
	
	@RequestMapping(value="/order/editPizza/{oid}", method = RequestMethod.POST)
	public ConfirmItem updateItem(@PathVariable("oid") Long orderId, @RequestBody OrderItem item) {
		try {
			ConfirmItem confirmItem = orderService.updateItem(item, orderId);
			return confirmItem;
		} catch(InvalidRequestException ire) {
			throw ire;
		}
	}
	
	@RequestMapping(value="/admin/addToppings", method = RequestMethod.PUT)
	public void addToppings( @RequestBody NewToppings toppings) {
		orderService.addToppings(toppings);
	}
	
	@RequestMapping(value="/admin/updateInventory", method = RequestMethod.PUT)
	public void updateInventory( @RequestBody InventoryUpdate inventoryUpdate) {
		try {
			orderService.updateInventory(inventoryUpdate);
		} catch(InvalidRequestException ire) {
			throw ire;
		}
	}
}
