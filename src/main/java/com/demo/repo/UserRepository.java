/**
 * 
 */
package com.demo.repo;

import org.springframework.data.repository.CrudRepository;

import com.demo.model.Users;

/**
 * @author 212473604
 *
 */

public interface UserRepository extends CrudRepository<Users, String>{
	Users findByUserName(String userName);
	Users findByLoginName(String loginName);
	Users findByDesignation(String designation);
}
