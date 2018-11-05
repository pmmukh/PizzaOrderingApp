package com.codesample.pizzaorder.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import com.codesample.pizzaorder.entities.Ingredient;
import com.codesample.pizzaorder.entities.InventoryUpdate;
import com.codesample.pizzaorder.entities.NewToppings;
import com.codesample.pizzaorder.entities.OrderItem;
import com.codesample.pizzaorder.entities.Top;
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
		
		//Check if all the required components of the pizza exist
		validateItems(item);
		
		//Check if all the given identifiers of components are valid
		Base base = baseRepo.getOne(item.getBaseId());
		if(base == null)
			throw new InvalidRequestException("No pizza base exists for the given ID");
		
		Cheese cheese = cheeseRepo.getOne(item.getCheeseId());
		if(cheese == null)
			throw new InvalidRequestException("No pizza cheese exists for the given ID");
		
		Sauce sauce = sauceRepo.getOne(item.getSauceId());
		if(sauce == null)
			throw new InvalidRequestException("No pizza sauce exists for the given ID");
		
		User user = userRepo.getOne(userId);
		if(user == null)
			throw new InvalidRequestException("No user exists for the given ID");
		
		List<Toppings> toppings = new ArrayList<Toppings>();
		if(item.getToppingIds() != null) {
			for(Long toppingId: item.getToppingIds()) {
				Toppings top = topRepo.getOne(toppingId);
				if( top == null)
					throw new InvalidRequestException("No topping exists for the given ID "+toppingId);
				toppings.add(top);
			}
		}
		
		//Create a new Selection object and add the components into it
		Selection selection = new Selection();
		selection.setBase(base);
		selection.setCheese(cheese);
		selection.setSauce(sauce);
		selection.setUser(user);
		selection.setStatus("CART");
		
		//Create price of the order by summing price of the components
		Integer price = base.getPrice()+cheese.getPrice()+sauce.getPrice();
		Integer topPrice = toppings.stream().mapToInt(p -> p.getPrice()).sum();
		price += topPrice;
		selection.setPrice(price);
		
		base.getSelection().add(selection);
		cheese.getSelection().add(selection);
		sauce.getSelection().add(selection);
		user.getSelection().add(selection);
		
		//Decrease quantities of each component
		base.setQuantity(base.getQuantity()-1);
		cheese.setQuantity(cheese.getQuantity()-1);
		sauce.setQuantity(sauce.getQuantity()-1);
		
		for(Toppings t:toppings) {
			t.getSelections().add(selection);
			t.setQuantity(t.getQuantity()-1);
		}
		
		//Save the order and all the components
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
	public ConfirmItem updateItem(OrderItem item,Long orderId) throws InvalidRequestException {
		if(item == null)
			throw new InvalidRequestException("No data provided to update the order");
		
		//Verify if the order to update exists
		Selection s = selectionRepo.getOne(orderId);
		if(s == null)
			throw new InvalidRequestException("No order exists for the given ID");
		Integer price = s.getPrice();
		
		//Replace the old base with new base, updating price and quantity
		if(item.getBaseId() != null && item.getBaseId() != s.getBase().getId()) {
			Base base = baseRepo.getOne(item.getBaseId());
			if(base == null)
				throw new InvalidRequestException("No pizza base exists for the given ID");
			
			Base oldbase = s.getBase();
			oldbase.getSelection().removeIf(p -> p.getId().equals(s.getId()));
			oldbase.setQuantity(oldbase.getQuantity()+1);
			base.setQuantity(base.getQuantity()-1);
			s.setBase(base);
			base.getSelection().add(s);
			price = price - oldbase.getPrice()+base.getPrice();
			baseRepo.save(base);
			baseRepo.save(oldbase);
		}
		
		//Repeat operation for cheese
		if(item.getCheeseId() != null && item.getCheeseId() != s.getCheese().getId()) {
			Cheese cheese = cheeseRepo.getOne(item.getCheeseId());
			if(cheese == null)
				throw new InvalidRequestException("No pizza cheese exists for the given ID");
			Cheese oldCheese = s.getCheese();
			oldCheese.getSelection().removeIf(p -> p.getId().equals(s.getId()));
			oldCheese.setQuantity(oldCheese.getQuantity()+1);
			cheese.setQuantity(cheese.getQuantity()-1);
			s.setCheese(cheese);
			cheese.getSelection().add(s);
			price = price - oldCheese.getPrice()+cheese.getPrice();
			cheeseRepo.save(cheese);
			cheeseRepo.save(oldCheese);
		}
		
		//Repeat operation for sauce
		if(item.getSauceId() != null && item.getSauceId() != s.getSauce().getId()) {
			Sauce sauce = sauceRepo.getOne(item.getSauceId());
			if(sauce == null)
				throw new InvalidRequestException("No pizza sauce exists for the given ID");
			Sauce oldSauce = s.getSauce();
			oldSauce.getSelection().removeIf(p -> p.getId().equals(s.getId()));
			oldSauce.setQuantity(oldSauce.getQuantity()+1);
			sauce.setQuantity(sauce.getQuantity()-1);
			s.setSauce(sauce);
			sauce.getSelection().add(s);
			price = price - oldSauce.getPrice()+sauce.getPrice();
			sauceRepo.save(sauce);
			sauceRepo.save(oldSauce);
		}
		
		//Remove all the old toppings, after removing the linked order ,decreasing the price and increasing quantity
		//Add in the new toppings, link each to the order, add to the price and increase quantity
		if(item.getToppingIds()!=null && !item.getToppingIds().isEmpty()) {
			List<Toppings> oldToppings = s.getToppings();
			for(Toppings top: oldToppings) {
				top.setQuantity(top.getQuantity()+1);
				top.getSelections().removeIf(p -> p.getId().equals(s.getId()));
				price = price - top.getPrice();
				topRepo.save(top);
			}
			
			List<Toppings> newToppings = new ArrayList<Toppings>();
			for(Long toppingId:item.getToppingIds()) {
				Toppings top = topRepo.getOne(toppingId);
				if( top == null)
					throw new InvalidRequestException("No topping exists for the given ID "+toppingId);
				top.getSelections().add(s);
				top.setQuantity(top.getQuantity()-1);
				price = price + top.getPrice();
				newToppings.add(top);
			}
			s.setToppings(newToppings);
		}
		s.setPrice(price);
		selectionRepo.save(s);
		
		//Create return object with id of the order, and the new price
		ConfirmItem confirmItem = new ConfirmItem();
		confirmItem.setSelectionId(orderId);
		confirmItem.setPrice(price);
		return confirmItem;
	}
	
	@Transactional
	public void addToppings(NewToppings toppings) {
		//Create new database object for each topping, set the details and save it
		for(Top t:toppings.getNewToppings()) {
			Toppings toppingDb = new Toppings();
			toppingDb.setName(t.getName());
			toppingDb.setPrice(t.getPrice());
			toppingDb.setQuantity(t.getQuantity());
			topRepo.save(toppingDb);
		}
	}
	
	@Transactional
	public void updateInventory(InventoryUpdate inventoryUpdate) {
		
		//Update the price and quantity, if provided, of each valid base
		if(!inventoryUpdate.getBases().isEmpty()) {
			for(Ingredient ing:inventoryUpdate.getBases()) {
				if(ing.getId() == null)
					throw new InvalidRequestException("ID required for base");
				Base base = baseRepo.getOne(ing.getId());
				if(base == null)
					throw new InvalidRequestException("No pizza base exists for the given ID "+ing.getId());
				if(ing.getPrice() != null)
					base.setPrice(ing.getPrice());
				if(ing.getQuantity() != null)
					base.setQuantity(ing.getQuantity());
				baseRepo.save(base);
			}
		}
		
		//Repeat for cheese
		if(!inventoryUpdate.getCheeses().isEmpty()) {
			for(Ingredient ing:inventoryUpdate.getCheeses()) {
				if(ing.getId() == null)
					throw new InvalidRequestException("ID required for cheese");
				Cheese cheese = cheeseRepo.getOne(ing.getId());
				if(cheese == null)
					throw new InvalidRequestException("No pizza cheese exists for the given ID "+ing.getId());
				if(ing.getPrice() != null)
					cheese.setPrice(ing.getPrice());
				if(ing.getQuantity() != null)
					cheese.setQuantity(ing.getQuantity());
				cheeseRepo.save(cheese);
			}
		}
		
		//Repeat for sauce
		if(!inventoryUpdate.getSauces().isEmpty()) {
			for(Ingredient ing:inventoryUpdate.getSauces()) {
				if(ing.getId() == null)
					throw new InvalidRequestException("ID required for sauce");
				Sauce sauce = sauceRepo.getOne(ing.getId());
				if(sauce == null)
					throw new InvalidRequestException("No pizza sauce exists for the given ID "+ing.getId());
				if(ing.getPrice() != null)
					sauce.setPrice(ing.getPrice());
				if(ing.getQuantity() != null)
					sauce.setQuantity(ing.getQuantity());
				sauceRepo.save(sauce);
			}
		}
		
		//Repeat for toppings
		if(!inventoryUpdate.getToppings().isEmpty()) {
			for(Ingredient ing:inventoryUpdate.getToppings()) {
				if(ing.getId() == null)
					throw new InvalidRequestException("ID required for topping");
				Toppings topping = topRepo.getOne(ing.getId());
				if(topping == null)
					throw new InvalidRequestException("No pizza topping exists for the given ID "+ing.getId());
				if(ing.getPrice() != null)
					topping.setPrice(ing.getPrice());
				if(ing.getQuantity() != null)
					topping.setQuantity(ing.getQuantity());
				topRepo.save(topping);
			}
		}
	}
	
}
