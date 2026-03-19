package com.thomasvitale.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class StoryPageController {

    @GetMapping({"/", "/story"})
    String storyPage() {
        return "story";
    }

}
