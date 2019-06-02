package de.hackathon.redis.repository;

import javax.inject.Inject;
import javax.inject.Named;

import redis.clients.jedis.JedisPool;

public class ConfigurationRepository {

	@Inject
	@Named("configuration")
	private JedisPool configurationPool;

	
}

