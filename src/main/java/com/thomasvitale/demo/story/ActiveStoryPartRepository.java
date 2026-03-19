package com.thomasvitale.demo.story;

import org.springframework.data.repository.CrudRepository;

public interface ActiveStoryPartRepository extends CrudRepository<ActiveStoryPart, Boolean> {
    
}
