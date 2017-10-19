package com.springms.cloud.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.springms.cloud.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

}
