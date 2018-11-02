package com.codesample.pizzaorder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codesample.pizzaorder.db.Toppings;

@Repository
public interface ToppingsRepository extends JpaRepository<Toppings,Long>{
}
