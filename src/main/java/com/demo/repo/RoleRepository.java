/**
 * 
 */
package com.demo.repo;

import org.springframework.data.repository.CrudRepository;

import com.demo.model.Role;

/**
 * @author 212473604
 *
 */
public interface RoleRepository extends CrudRepository<Role, String>{
	Role findByRole(String role);
}
