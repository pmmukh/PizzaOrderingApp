package com.codesample.pizzaorder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.codesample.pizzaorder.db.Sauce;

@Repository
public interface SauceRepository extends JpaRepository<Sauce,Long>{

}
