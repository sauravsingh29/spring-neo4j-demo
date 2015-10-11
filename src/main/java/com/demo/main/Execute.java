package com.demo.main;

import java.io.File;
import java.io.IOException;

import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.impl.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.data.neo4j.support.Neo4jTemplate;

import com.demo.model.Role;
import com.demo.model.Users;
import com.demo.model.rel.ReadWriteRelation;
import com.demo.repo.RoleRepository;
import com.demo.repo.UserRepository;

/*
@Configuration
@ComponentScan(basePackages = "com.demo")
@EnableAutoConfiguration*/
@SpringBootApplication
@ComponentScan(basePackages = "com.demo")
public class Execute implements CommandLineRunner {

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	GraphDatabase graphDatabase;

	@Autowired
	Neo4jTemplate neo4jTemplate;

	public void run(String... args) throws Exception {
		
		Users greg = new Users("Greg");
		Users roy = new Users("Roy");
		Users craig = new Users("Craig");
		Users greg1 = new Users("Greg");
		Users roy1 = new Users("Roy");
		Users ss = new Users("Saurav");
		Users[] people = new Users[] { greg, roy, craig, greg1, roy1, ss };

		Role admin = new Role("Admin");
		Role analyst = new Role("Analyst");
		Role user = new Role("User");
		Role[] roles = new Role[] { admin, analyst, user };

		System.out.println("Before linking...");
		for (Role role : roles) {
			System.out.println(role);
		}

		Transaction tx = graphDatabase.beginTx();
		try {
			System.out.println("Saving User...");
			for (Users us : people) {
				if (userRepository.findByName(us.getName()) == null) {
					userRepository.save(us);
				} else {
					System.out.println("Oops..!! User already exsits");
				}
			}

			System.out.println("Saving Role...");
			for (Role role : roles) {
				if (roleRepository.findByRole(role.getRole()) == null) {
					roleRepository.save(role);
				} else {
					System.out.println("Oops..!! Role already exsits");
				}
			}

			System.out.println("Lookup each user by name...");
			for (Users us : userRepository.findAll()) {
				System.out.println("ID " + userRepository.findByName(us.getName()).getId() + " User "
						+ userRepository.findByName(us.getName()).getName());
			}

			System.out.println("Lookup each role by name...");
			for (Role role : roleRepository.findAll()) {
				System.out.println("ID " + roleRepository.findByRole(role.getRole()).getId() + " Role "
						+ roleRepository.findByRole(role.getRole()).getRole());
			}
			neo4jTemplate.createRelationshipBetween(userRepository.findByName("Saurav"),
					roleRepository.findByRole("Admin"), ReadWriteRelation.class, "READ_WRITE", false);
			neo4jTemplate.createRelationshipBetween(userRepository.findByName("Roy"),
					roleRepository.findByRole("Admin"), ReadWriteRelation.class, "READ_WRITE", false);
			neo4jTemplate.createRelationshipBetween(userRepository.findByName("Craig"),
					roleRepository.findByRole("Analyst"), ReadWriteRelation.class, "READ_ONLY", false);
			neo4jTemplate.createRelationshipBetween(userRepository.findByName("Craig"),
					roleRepository.findByRole("User"), ReadWriteRelation.class, "READ_ONLY", false);
			neo4jTemplate.createRelationshipBetween(userRepository.findByName("Saurav"),
					roleRepository.findByRole("Analyst"), ReadWriteRelation.class, "READ_ONLY", false);
			neo4jTemplate.createRelationshipBetween(userRepository.findByName("Roy"),
					roleRepository.findByRole("Analyst"), ReadWriteRelation.class, "READ_ONLY", false);

			tx.success();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tx.close();
		}
	}

	public static void main(String[] args) throws IOException {
		FileUtils.deleteRecursively(new File("C:\\temp\\neo\\neo.db"));

		SpringApplication.run(Execute.class, args);
	}

}
