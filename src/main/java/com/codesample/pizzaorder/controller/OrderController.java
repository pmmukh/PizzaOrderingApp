package com.codesample.pizzaorder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codesample.pizzaorder.entities.ConfirmItem;
import com.codesample.pizzaorder.entities.OrderItem;
import com.codesample.pizzaorder.exceptions.InvalidRequestException;
import com.codesample.pizzaorder.service.OrderService;

@RestController
public class OrderController {

	@Autowired
	OrderService orderService;
	
	@RequestMapping(value="/addPizza/{uid}", method = RequestMethod.POST)
	public ConfirmItem addItem(@PathVariable("uid") Long userId, @RequestBody OrderItem item) {
		try {
			ConfirmItem confirmItem = orderService.addItem(item, userId);
			return confirmItem;
		} catch(InvalidRequestException ire) {
			throw ire;
		}
	}
}
