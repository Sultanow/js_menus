package de.hackathon.redis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.hackathon.redis.data.Configuration;
 
@Repository
public interface ConfigurationRepository extends CrudRepository<Configuration, String> {}
