package com.demo.config;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.support.Neo4jTemplate;

@Configuration
@EnableNeo4jRepositories(basePackages = "com.demo")
public class NeoConfig extends Neo4jConfiguration {

	public NeoConfig() {
		setBasePackage("com.demo");
	}
	
	@Bean
	GraphDatabaseService graphDatabaseService() {
		return new GraphDatabaseFactory().newEmbeddedDatabaseBuilder("C:\\temp\\neo_demo\\neo.db").setConfig(GraphDatabaseSettings.allow_store_upgrade, "true").newGraphDatabase();
	}
	
	@Bean
	Neo4jTemplate getNeo4jTemplate(){
		return new Neo4jTemplate(getGraphDatabaseService());
	}

}
