package com.demo.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.impl.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.util.CollectionUtils;

import com.demo.model.Role;
import com.demo.model.Users;
import com.demo.model.rel.ReadWriteRelation;
import com.demo.mssql.constant.MSSQLConstant;
import com.demo.mssql.model.MSSQLRole;
import com.demo.mssql.model.MSSQLUser;
import com.demo.mssql.service.MSSQLService;
import com.demo.repo.RoleRepository;
import com.demo.repo.UserRepository;

@SpringBootApplication
@ComponentScan(basePackages = "com.demo")
public class ExecuteDemo implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(ExecuteDemo.class);
	@Autowired
	RoleRepository roleRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	GraphDatabase graphDatabase;

	@Autowired
	Neo4jTemplate neo4jTemplate;

	@Autowired
	@Qualifier("msSQLService")
	MSSQLService mssqlService;

	@Override
	public void run(String... args) throws Exception {

		logger.info("Mapping data from MSSQL Server to Neo4J object started..");
		List<Users> users = userFromMSSQLToNeo4J();

		List<Role> roles = roleFromMSSQLToNeo4J();
		logger.info("Mapping data from MSSQL Server to Neo4J object finished..");

		Transaction tx = graphDatabase.beginTx();
		try {
			if (!CollectionUtils.isEmpty(users)) {

				for (Users us : users) {
					if (userRepository.findByLoginName(us.getLoginName()) == null) {
						logger.info("Saving user from [{}] to [{}]..", us.getClass(), "Neo4J Database");
						userRepository.save(us);
					} else {
						logger.warn("Hey!! User already exists by login name [{}]", us.getLoginName());
					}
				}

			}

			if (!CollectionUtils.isEmpty(roles)) {
				for (Role role : roles) {
					if (roleRepository.findByRoleName(role.getRoleName()) == null) {
						logger.info("Saving role from [{}] to [{}]..", role.getClass(), "Neo4J Database");
						roleRepository.save(role);
					} else {
						logger.warn("Hey!! Role [{}] already exists", role.getRoleName());
					}
				}
			}
			createRelationship();

			tx.success();
		} catch (Exception e) {
			logger.error("Exception occured, Message [{}]", e.getMessage());
		} finally {
			logger.info("Alright!, Time to shutdown transaction...");
			tx.close();
		}
	}

	/**
	 * Create relationship between nodes
	 */
	private void createRelationship() {
		logger.info("WOhoo..!, Time to Create relationship between nodes...");
		neo4jTemplate.createRelationshipBetween(userRepository.findByLoginName("TLambright"),
				roleRepository.findByRoleName(MSSQLConstant.Roles.ADMIN), ReadWriteRelation.class, "READ_WRITE", false);
		neo4jTemplate.createRelationshipBetween(userRepository.findByLoginName("TLambright"),
				roleRepository.findByRoleName(MSSQLConstant.Roles.ANALYST), ReadWriteRelation.class, "READ_ONLY", false);
		neo4jTemplate.createRelationshipBetween(userRepository.findByLoginName("SFisher"),
				roleRepository.findByRoleName(MSSQLConstant.Roles.ADMIN), ReadWriteRelation.class, "READ_WRITE", false);
		neo4jTemplate.createRelationshipBetween(userRepository.findByLoginName("SFisher"),
				roleRepository.findByRoleName(MSSQLConstant.Roles.ANALYST), ReadWriteRelation.class, "READ_ONLY", false);
		neo4jTemplate.createRelationshipBetween(userRepository.findByLoginName("YHuang"),
				roleRepository.findByRoleName(MSSQLConstant.Roles.ADMIN), ReadWriteRelation.class, "READ_WRITE", false);
		logger.info("Ok..!, Relationship creation between nodes done...");
	}

	/**
	 * @return
	 */
	private List<Role> roleFromMSSQLToNeo4J() {
		logger.info("Fetch MSSQL Server Users by Neo4j..");
		List<MSSQLRole> mssqlRoles = mssqlService.getAllRoles();
		List<Role> roles = new ArrayList<Role>(0);
		for (MSSQLRole mssqlRole : mssqlRoles) {
			Role role = new Role();
			BeanUtils.copyProperties(mssqlRole, role);
			roles.add(role);
			logger.info("Fetch MSSQL Server Users [{}] by Neo4j..", mssqlRoles.size());
		}
		return roles;
	}

	/**
	 * @return
	 */
	private List<Users> userFromMSSQLToNeo4J() {
		logger.info("Fetch MSSQL Server Users by Neo4j..");
		List<MSSQLUser> mssqlUsers = mssqlService.getAllUsers();
		List<Users> users = new ArrayList<Users>(0);
		for (MSSQLUser mu : mssqlUsers) {
			Users usr = new Users();
			BeanUtils.copyProperties(mu, usr);
			users.add(usr);
		}
		logger.info("Fetched MSSQL Server Users [{}] by Neo4j..", users.size());
		return users;
	}

	public static void main(String[] args) throws IOException {
		FileUtils.deleteRecursively(new File("C:\\temp\\neo_demo\\neo.db"));
		SpringApplication.run(ExecuteDemo.class, args);
	}

}
