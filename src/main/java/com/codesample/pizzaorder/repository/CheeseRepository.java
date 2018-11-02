package com.codesample.pizzaorder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.codesample.pizzaorder.db.Cheese;

@Repository
public interface CheeseRepository extends JpaRepository<Cheese,Long>{

}
