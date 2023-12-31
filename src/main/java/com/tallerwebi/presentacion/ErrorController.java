package com.tallerwebi.presentacion;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("error")
public class ErrorController {
    private final HttpSession session;

    public ErrorController(HttpSession session) { this.session = session;}

    @GetMapping
    public ModelAndView getErrorController(@RequestParam("errorMessage") String errorMessage){
        ModelMap model = new ModelMap();
        model.put("error", errorMessage);
        return new ModelAndView("error", model);
    }

}
