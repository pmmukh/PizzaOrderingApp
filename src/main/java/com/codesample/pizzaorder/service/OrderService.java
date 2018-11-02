package com.codesample.pizzaorder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codesample.pizzaorder.db.Base;
import com.codesample.pizzaorder.db.Cheese;
import com.codesample.pizzaorder.db.Sauce;
import com.codesample.pizzaorder.db.Selection;
import com.codesample.pizzaorder.db.User;
import com.codesample.pizzaorder.entities.ConfirmItem;
import com.codesample.pizzaorder.entities.OrderItem;
import com.codesample.pizzaorder.exceptions.InvalidRequestException;
import com.codesample.pizzaorder.repository.BaseRepository;
import com.codesample.pizzaorder.repository.CheeseRepository;
import com.codesample.pizzaorder.repository.SauceRepository;
import com.codesample.pizzaorder.repository.SelectionRepository;
import com.codesample.pizzaorder.repository.UserRepository;

@Service
public class OrderService {

	@Autowired
	BaseRepository baseRepo;
	
	@Autowired
	CheeseRepository cheeseRepo;
	
	@Autowired
	SauceRepository sauceRepo;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	SelectionRepository selectionRepo;
	
	@Transactional
	public ConfirmItem addItem(OrderItem item,Long userId) throws InvalidRequestException {
		ConfirmItem confirmItem = new ConfirmItem();
		
		validateItems(item);
		Base base = baseRepo.getOne(item.getBaseId());
		Cheese cheese = cheeseRepo.getOne(item.getCheeseId());
		Sauce sauce = sauceRepo.getOne(item.getSauceId());
		User user = userRepo.getOne(userId);
		
		if(base == null)
			throw new InvalidRequestException("No pizza base exists for the given ID");
		if(cheese == null)
			throw new InvalidRequestException("No pizza cheese exists for the given ID");
		if(sauce == null)
			throw new InvalidRequestException("No pizza sauce exists for the given ID");
		if(user == null)
			throw new InvalidRequestException("No user exists for the given ID");
		
		Selection selection = new Selection();
		selection.setBase(base);
		selection.setCheese(cheese);
		selection.setSauce(sauce);
		selection.setUser(user);
		base.getSelection().add(selection);
		cheese.getSelection().add(selection);
		sauce.getSelection().add(selection);
		user.getSelection().add(selection);
		base.setQuantity(base.getQuantity()-1);
		cheese.setQuantity(cheese.getQuantity()-1);
		sauce.setQuantity(sauce.getQuantity()-1);
		Selection s = selectionRepo.save(selection);
		baseRepo.save(base);
		cheeseRepo.save(cheese);
		sauceRepo.save(sauce);
		userRepo.save(user);
		confirmItem.setSelectionId(s.getId());
		Integer price = base.getPrice()+cheese.getPrice()+sauce.getPrice();
		confirmItem.setPrice(price);
		return confirmItem;
	}

	private void validateItems(OrderItem item) {
		if(item == null)
			throw new InvalidRequestException("No request body provided");
		if(item.getBaseId() == null)
			throw new InvalidRequestException("No base ID provided");
		if(item.getCheeseId() == null)
			throw new InvalidRequestException("No cheese ID provided");
		if(item.getSauceId() == null)
			throw new InvalidRequestException("No sauce ID provided");
				
	}
}
