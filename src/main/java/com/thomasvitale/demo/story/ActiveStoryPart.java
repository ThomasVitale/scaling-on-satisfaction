package com.thomasvitale.demo.story;

import org.springframework.data.annotation.Id;

public record ActiveStoryPart(@Id Boolean id, int part) {
    
}
