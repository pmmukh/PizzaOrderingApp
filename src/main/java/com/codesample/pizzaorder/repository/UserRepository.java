package com.codesample.pizzaorder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codesample.pizzaorder.db.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long>{
}
