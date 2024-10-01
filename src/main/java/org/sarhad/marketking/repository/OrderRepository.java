package org.sarhad.marketking.repository;

import org.sarhad.marketking.model.Orders;
import org.sarhad.marketking.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByUser(Users user);
}