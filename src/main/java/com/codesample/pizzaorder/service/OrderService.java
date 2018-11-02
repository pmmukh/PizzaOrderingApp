package com.codesample.pizzaorder.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codesample.pizzaorder.db.Base;
import com.codesample.pizzaorder.db.Cheese;
import com.codesample.pizzaorder.db.Sauce;
import com.codesample.pizzaorder.db.Selection;
import com.codesample.pizzaorder.db.Toppings;
import com.codesample.pizzaorder.db.User;
import com.codesample.pizzaorder.entities.ConfirmItem;
import com.codesample.pizzaorder.entities.NewToppings;
import com.codesample.pizzaorder.entities.OrderItem;
import com.codesample.pizzaorder.entities.Topping;
import com.codesample.pizzaorder.exceptions.InvalidRequestException;
import com.codesample.pizzaorder.repository.BaseRepository;
import com.codesample.pizzaorder.repository.CheeseRepository;
import com.codesample.pizzaorder.repository.SauceRepository;
import com.codesample.pizzaorder.repository.SelectionRepository;
import com.codesample.pizzaorder.repository.ToppingsRepository;
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
	
	@Autowired
	ToppingsRepository topRepo;
	
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
		
		List<Toppings> toppings = new ArrayList<Toppings>();
		for(Long toppingId: item.getToppingIds()) {
			Toppings top = topRepo.getOne(toppingId);
			if( top == null)
				throw new InvalidRequestException("No topping exists for the given ID "+toppingId);
			toppings.add(top);
		}
		Selection selection = new Selection();
		selection.setBase(base);
		selection.setCheese(cheese);
		selection.setSauce(sauce);
		selection.setUser(user);
		selection.setStatus("CART");
		Integer price = base.getPrice()+cheese.getPrice()+sauce.getPrice();
		Integer topPrice = toppings.stream().mapToInt(p -> p.getPrice()).sum();
		price += topPrice;
		selection.setPrice(price);
		base.getSelection().add(selection);
		cheese.getSelection().add(selection);
		sauce.getSelection().add(selection);
		user.getSelection().add(selection);
		base.setQuantity(base.getQuantity()-1);
		cheese.setQuantity(cheese.getQuantity()-1);
		sauce.setQuantity(sauce.getQuantity()-1);
		for(Toppings t:toppings) {
			t.getSelections().add(selection);
			t.setQuantity(t.getQuantity()-1);
		}
		selection.setToppings(toppings);
		Selection s = selectionRepo.save(selection);
		baseRepo.save(base);
		cheeseRepo.save(cheese);
		sauceRepo.save(sauce);
		userRepo.save(user);
		confirmItem.setSelectionId(s.getId());
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
	
	@Transactional
	public void updateItem(OrderItem item,Long orderId) throws InvalidRequestException {
		Selection s = selectionRepo.getOne(orderId);
		if(s == null)
			throw new InvalidRequestException("No order exists for the given ID");
		if(item == null)
			throw new InvalidRequestException("No data provided to update the order");
		
		if(item.getBaseId() != null && item.getBaseId() != s.getBase().getId()) {
			Base base = baseRepo.getOne(item.getBaseId());
			Base oldbase = s.getBase();
			oldbase.getSelection().removeIf(p -> p.getId().equals(s.getId()));
			s.setBase(base);
			base.getSelection().add(s);
			baseRepo.save(base);
			baseRepo.save(oldbase);
		}
		if(item.getCheeseId() != null && item.getCheeseId() != s.getCheese().getId()) {
			Cheese cheese = cheeseRepo.getOne(item.getCheeseId());
			Cheese oldCheese = s.getCheese();
			oldCheese.getSelection().removeIf(p -> p.getId().equals(s.getId()));
			s.setCheese(cheese);
			cheese.getSelection().add(s);
			cheeseRepo.save(cheese);
			cheeseRepo.save(oldCheese);
		}
		if(item.getSauceId() != null && item.getSauceId() != s.getSauce().getId()) {
			Sauce sauce = sauceRepo.getOne(item.getSauceId());
			Sauce oldSauce = s.getSauce();
			oldSauce.getSelection().removeIf(p -> p.getId().equals(s.getId()));
			s.setSauce(sauce);
			sauce.getSelection().add(s);
			sauceRepo.save(sauce);
			sauceRepo.save(oldSauce);
		}
	}
	
	@Transactional
	public void addToppings(NewToppings toppings) {
		for(Topping t:toppings.getNewToppings()) {
			Toppings toppingDb = new Toppings();
			toppingDb.setName(t.getName());
			toppingDb.setPrice(t.getPrice());
			toppingDb.setQuantity(t.getQuantity());
			topRepo.save(toppingDb);
		}
	}
	
}
