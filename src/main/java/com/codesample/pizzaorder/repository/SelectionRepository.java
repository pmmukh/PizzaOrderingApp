package com.codesample.pizzaorder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codesample.pizzaorder.db.Selection;

@Repository
public interface SelectionRepository extends JpaRepository<Selection,Long>{
}
