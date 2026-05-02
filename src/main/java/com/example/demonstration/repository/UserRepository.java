package com.example.demonstration.repository;

import com.example.demonstration.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    
    // Spring Data JPA magic: It automatically creates the SQL query 
    // "SELECT * FROM user WHERE username = ?" based on this method name.
    User findByUsername(String username);
}