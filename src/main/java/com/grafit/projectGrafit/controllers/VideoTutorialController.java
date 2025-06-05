package com.grafit.projectGrafit.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VideoTutorialController {
    
     @GetMapping("/tutorials/press-banca")
    public String showPressBanca() {
        return "tutorials/press-banca"; 
    }

    @GetMapping("/tutorials/peso-muerto")
    public String showPesoMuerto() {
        return "tutorials/peso-muerto"; 
    }

    @GetMapping("/tutorials/sentadilla")
    public String showSentadilla() {
        return "tutorials/sentadilla";
    }

}
