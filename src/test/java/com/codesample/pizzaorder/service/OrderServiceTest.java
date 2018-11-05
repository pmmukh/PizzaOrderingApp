package com.codesample.pizzaorder.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
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


@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTest {
	
	@InjectMocks
	OrderService orderService;
	
	@Mock
	BaseRepository baseRepo;
	
	@Mock
	CheeseRepository cheeseRepo;
	
	@Mock
	SauceRepository sauceRepo;
	
	@Mock
	UserRepository userRepo;
	
	@Mock
	SelectionRepository selectionRepo;
	
	@Mock
	ToppingsRepository topRepo;
	
	@Test(expected = InvalidRequestException.class)
	public void testAddItemNoOrder() {
		orderService.addItem(null, new Long(1000));
	}
	
	@Test(expected = InvalidRequestException.class)
	public void testAddItemNoBase() {
		OrderItem orderItem = getItem();
		orderItem.setBaseId(null);
		orderService.addItem(orderItem, new Long(1000));
	}

	@Test(expected = InvalidRequestException.class)
	public void testAddItemNoCheese() {
		OrderItem orderItem = getItem();
		orderItem.setCheeseId(null);
		orderService.addItem(orderItem, new Long(1000));
	}
	
	@Test(expected = InvalidRequestException.class)
	public void testAddItemNoSauce() {
		OrderItem orderItem = getItem();
		orderItem.setSauceId(null);
		orderService.addItem(orderItem, new Long(1000));
	}
	
	@Test(expected = InvalidRequestException.class)
	public void testAddItemNoBaseForId() {
		OrderItem orderItem = getItem();
		when(baseRepo.getOne(anyLong())).thenReturn(null);
		orderService.addItem(orderItem, new Long(1000));
	}
	
	@Test(expected = InvalidRequestException.class)
	public void testAddItemNoCheeseForId() {
		OrderItem orderItem = getItem();
		when(baseRepo.getOne(anyLong())).thenReturn(new Base());
		when(cheeseRepo.getOne(anyLong())).thenReturn(null);
		
		orderService.addItem(orderItem, new Long(1000));
	}
	@Test(expected = InvalidRequestException.class)
	public void testAddItemNoSauceForId() {
		OrderItem orderItem = getItem();
		when(baseRepo.getOne(anyLong())).thenReturn(new Base());
		when(cheeseRepo.getOne(anyLong())).thenReturn(new Cheese());
		when(sauceRepo.getOne(anyLong())).thenReturn(null);
		orderService.addItem(orderItem, new Long(1000));
	}
	@Test(expected = InvalidRequestException.class)
	public void testAddItemNoUserForId() {
		OrderItem orderItem = getItem();
		when(baseRepo.getOne(anyLong())).thenReturn(new Base());
		when(cheeseRepo.getOne(anyLong())).thenReturn(new Cheese());
		when(sauceRepo.getOne(anyLong())).thenReturn(new Sauce());
		when(userRepo.getOne(anyLong())).thenReturn(null);
		orderService.addItem(orderItem, new Long(1000));
	}
	
	@Test(expected = InvalidRequestException.class)
	public void testAddItemNoToppingrForId() {
		OrderItem orderItem = getItem();
		when(baseRepo.getOne(anyLong())).thenReturn(new Base());
		when(cheeseRepo.getOne(anyLong())).thenReturn(new Cheese());
		when(sauceRepo.getOne(anyLong())).thenReturn(new Sauce());
		when(userRepo.getOne(anyLong())).thenReturn(new User());
		when(topRepo.getOne(anyLong())).thenReturn(null);
		orderService.addItem(orderItem, new Long(1000));
	}
	
	@Test
	public void testAddItem() {
		OrderItem orderItem = getItem();
		when(baseRepo.getOne(anyLong())).thenReturn(getBase());
		when(cheeseRepo.getOne(anyLong())).thenReturn(getCheese());
		when(sauceRepo.getOne(anyLong())).thenReturn(getSauce());
		when(userRepo.getOne(anyLong())).thenReturn(new User());
		when(topRepo.getOne(anyLong())).thenReturn(getTopping());
		
		Selection sel = new Selection();
		sel.setId(new Long(1000));
		when(selectionRepo.save(any(Selection.class))).thenReturn(sel);
		when(baseRepo.save(any(Base.class))).thenReturn(null);
		when(cheeseRepo.save(any(Cheese.class))).thenReturn(null);
		when(sauceRepo.save(any(Sauce.class))).thenReturn(null);
		when(userRepo.save(any(User.class))).thenReturn(null);
		ConfirmItem confirmItem = orderService.addItem(orderItem, new Long(1000));
		
		Assert.assertEquals(new Long(1000), confirmItem.getSelectionId());
		Assert.assertEquals(new Integer(80), confirmItem.getPrice());
	}
	
	@Test(expected = InvalidRequestException.class)
	public void testUpdateItemNoItem() {
		orderService.updateItem(null, new Long(1000));
	}
	
	@Test(expected = InvalidRequestException.class)
	public void testUpdateItemNoOrder() {
		when(selectionRepo.getOne(anyLong())).thenReturn(null);
		orderService.updateItem(getItem(), new Long(1000));
	}
	
	@Test(expected = InvalidRequestException.class)
	public void testUpdateItemNoBase() {
		when(selectionRepo.getOne(anyLong())).thenReturn(getSelection());
		when(baseRepo.getOne(anyLong())).thenReturn(null);
		orderService.updateItem(getItem(), new Long(1000));
	}
	
	@Test(expected = InvalidRequestException.class)
	public void testUpdateItemNoCheese() {
		when(selectionRepo.getOne(anyLong())).thenReturn(getSelection());
		when(cheeseRepo.getOne(anyLong())).thenReturn(null);
		OrderItem orderItem = getItem();
		orderItem.setBaseId(null);
		orderService.updateItem(orderItem, new Long(1000));
	}
	
	@Test(expected = InvalidRequestException.class)
	public void testUpdateItemNoSauce() {
		when(selectionRepo.getOne(anyLong())).thenReturn(getSelection());
		when(sauceRepo.getOne(anyLong())).thenReturn(null);
		OrderItem orderItem = getItem();
		orderItem.setBaseId(null);
		orderItem.setCheeseId(null);
		orderService.updateItem(orderItem, new Long(1000));
	}
	
	@Test(expected = InvalidRequestException.class)
	public void testUpdateItemNoToppings() {
		when(selectionRepo.getOne(anyLong())).thenReturn(getSelection());
		when(topRepo.getOne(anyLong())).thenReturn(null);
		when(topRepo.save(any(Toppings.class))).thenReturn(null);
		OrderItem orderItem = getItem();
		orderItem.setBaseId(null);
		orderItem.setCheeseId(null);
		orderItem.setSauceId(null);
		orderService.updateItem(orderItem, new Long(1000));
	}
	
	@Test
	public void testUpdateItem() {
		when(selectionRepo.getOne(anyLong())).thenReturn(getSelection());
		when(baseRepo.getOne(anyLong())).thenReturn(getBase2());
		when(baseRepo.save(any(Base.class))).thenReturn(null);
		
		when(cheeseRepo.getOne(anyLong())).thenReturn(getCheese2());
		when(cheeseRepo.save(any(Cheese.class))).thenReturn(null);
		
		when(sauceRepo.getOne(anyLong())).thenReturn(getSauce2());
		when(sauceRepo.save(any(Sauce.class))).thenReturn(null);
		
		when(topRepo.getOne(anyLong())).thenReturn(getTopping2());
		when(topRepo.save(any(Toppings.class))).thenReturn(null);
		when(selectionRepo.save(any(Selection.class))).thenReturn(null);
		OrderItem orderItem = getItem();
		ConfirmItem confirmItem = orderService.updateItem(orderItem, new Long(1000));
		Assert.assertEquals(new Integer(100), confirmItem.getPrice());
	}
	
	@Test
	public void testAddToppings() {
		Top t = new Top();
		t.setName("Bacon");
		t.setPrice(new Integer(25));
		t.setQuantity(new Integer(15));
		NewToppings newTops = new NewToppings();
		newTops.getNewToppings().add(t);
		when(topRepo.save(any(Toppings.class))).thenReturn(null);
		orderService.addToppings(newTops);
	}
	
	@Test(expected = InvalidRequestException.class)
	public void testUpdateInventoryNoIdForBase() {
		InventoryUpdate invUpdate = new InventoryUpdate();
		invUpdate.getBases().add(new Ingredient());
		orderService.updateInventory(invUpdate);
	}
	
	@Test(expected = InvalidRequestException.class)
	public void testUpdateInventoryNoIdForCheese() {
		InventoryUpdate invUpdate = new InventoryUpdate();
		invUpdate.getCheeses().add(new Ingredient());
		orderService.updateInventory(invUpdate);
	}
	
	@Test(expected = InvalidRequestException.class)
	public void testUpdateInventoryNoIdForSauce() {
		InventoryUpdate invUpdate = new InventoryUpdate();
		invUpdate.getSauces().add(new Ingredient());
		orderService.updateInventory(invUpdate);
	}
	
	@Test(expected = InvalidRequestException.class)
	public void testUpdateInventoryNoIdForTopping() {
		InventoryUpdate invUpdate = new InventoryUpdate();
		invUpdate.getToppings().add(new Ingredient());
		orderService.updateInventory(invUpdate);
	}
	
	@Test(expected = InvalidRequestException.class)
	public void testUpdateInventoryNoBaseExists() {
		InventoryUpdate invUpdate = new InventoryUpdate();
		invUpdate.getBases().add(getIng());
		when(baseRepo.getOne(anyLong())).thenReturn(null);
		orderService.updateInventory(invUpdate);
	}
	
	@Test(expected = InvalidRequestException.class)
	public void testUpdateInventoryNoCheeseExists() {
		InventoryUpdate invUpdate = new InventoryUpdate();
		invUpdate.getCheeses().add(getIng());
		when(cheeseRepo.getOne(anyLong())).thenReturn(null);
		orderService.updateInventory(invUpdate);
	}
	
	@Test(expected = InvalidRequestException.class)
	public void testUpdateInventoryNoSauceExists() {
		InventoryUpdate invUpdate = new InventoryUpdate();
		invUpdate.getSauces().add(getIng());
		when(sauceRepo.getOne(anyLong())).thenReturn(null);
		orderService.updateInventory(invUpdate);
	}
	
	@Test(expected = InvalidRequestException.class)
	public void testUpdateInventoryNoToppingExists() {
		InventoryUpdate invUpdate = new InventoryUpdate();
		invUpdate.getToppings().add(getIng());
		when(topRepo.getOne(anyLong())).thenReturn(null);
		orderService.updateInventory(invUpdate);
	}
	
	@Test
	public void testUpdateInventory() {
		InventoryUpdate invUpdate = new InventoryUpdate();
		invUpdate.getBases().add(getIng());
		invUpdate.getCheeses().add(getIng());
		invUpdate.getSauces().add(getIng());
		invUpdate.getToppings().add(getIng());
		
		when(baseRepo.getOne(anyLong())).thenReturn(getBase());
		when(cheeseRepo.getOne(anyLong())).thenReturn(getCheese());
		when(sauceRepo.getOne(anyLong())).thenReturn(getSauce());
		when(userRepo.getOne(anyLong())).thenReturn(new User());
		when(topRepo.getOne(anyLong())).thenReturn(getTopping());
		
		when(baseRepo.save(any(Base.class))).thenReturn(null);
		when(cheeseRepo.save(any(Cheese.class))).thenReturn(null);
		when(sauceRepo.save(any(Sauce.class))).thenReturn(null);
		when(topRepo.save(any(Toppings.class))).thenReturn(null);
		
		orderService.updateInventory(invUpdate);
	}
	
	private Ingredient getIng() {
		Ingredient ing  = new Ingredient();
		ing.setId(new Long(1000));
		ing.setPrice(new Integer(20));
		ing.setQuantity(new Integer(30));
		return ing;
	}
	
	private InventoryUpdate getInvUpdate() {
		InventoryUpdate invUpdate = new InventoryUpdate();
		
		return invUpdate;
	}
	
	private OrderItem getItem() {
		OrderItem orderItem = new OrderItem();
		orderItem.setBaseId(new Long(1000));
		orderItem.setCheeseId(new Long(1000));
		orderItem.setSauceId(new Long(1000));
		List<Long> toppings = new ArrayList<Long>();
		toppings.add(new Long(1000));
		orderItem.setToppingIds(toppings);
		return orderItem;
	}
	
	private Base getBase() {
		Base base = new Base();
		base.setId(new Long(2000));
		base.setPrice(new Integer(20));
		base.setQuantity(new Integer(20));
		return base;
	}
	
	private Base getBase2() {
		Base base = new Base();
		base.setId(new Long(1000));
		base.setPrice(new Integer(25));
		base.setQuantity(new Integer(20));
		return base;
	}
	
	private Cheese getCheese() {
		Cheese cheese = new Cheese();
		cheese.setId(new Long(2000));
		cheese.setPrice(new Integer(20));
		cheese.setQuantity(new Integer(20));
		return cheese;
	}
	
	private Cheese getCheese2() {
		Cheese cheese = new Cheese();
		cheese.setId(new Long(1000));
		cheese.setPrice(new Integer(25));
		cheese.setQuantity(new Integer(20));
		return cheese;
	}
	
	private Sauce getSauce() {
		Sauce sauce = new Sauce();
		sauce.setId(new Long(2000));
		sauce.setPrice(new Integer(20));
		sauce.setQuantity(new Integer(20));
		return sauce;
	}
	
	private Sauce getSauce2() {
		Sauce sauce = new Sauce();
		sauce.setId(new Long(1000));
		sauce.setPrice(new Integer(25));
		sauce.setQuantity(new Integer(20));
		return sauce;
	}
	
	private Toppings getTopping() {
		Toppings topping = new Toppings();
		topping.setPrice(new Integer(20));
		topping.setQuantity(new Integer(20));
		return topping;
	}
	
	private Toppings getTopping2() {
		Toppings topping = new Toppings();
		topping.setPrice(new Integer(25));
		topping.setQuantity(new Integer(20));
		return topping;
	}
	
	private Selection getSelection() {
		Selection sel = new Selection();
		sel.setPrice(new Integer(80));
		sel.setBase(getBase());
		sel.setCheese(getCheese());
		sel.setSauce(getSauce());
		List<Toppings> tops = new ArrayList<Toppings>();
		tops.add(getTopping());
		sel.setToppings(tops);
		return sel;
	}
}
