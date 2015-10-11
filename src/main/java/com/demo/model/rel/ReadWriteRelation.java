package com.demo.model.rel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.demo.model.Role;
import com.demo.model.Users;

@RelationshipEntity(type = "READ_WRITE")
public class ReadWriteRelation {

	@GraphId Long id;

	@StartNode
	public Users users;

	@EndNode
	public Role role;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the users
	 */
	public Users getUsers() {
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(Users users) {
		this.users = users;
	}

	/**
	 * @return the role
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(Role role) {
		this.role = role;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReadWriteRelation [id=");
		builder.append(id);
		builder.append(", users=");
		builder.append(users);
		builder.append(", role=");
		builder.append(role);
		builder.append("]");
		return builder.toString();
	}

	
}
