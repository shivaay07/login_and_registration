package com.duttech.authentication.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.duttech.authentication.models.User;
import com.duttech.authentication.services.UserService;
import com.duttech.authentication.validator.UserValidator;

@Controller
public class UserController {
	private final UserService userService;
	
	private final UserValidator userValidator;
    
    public UserController(UserService userService, UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }
    
    @RequestMapping("/registration")
    public String registerForm(@ModelAttribute("user") User user) {
        return "registration.jsp";
    }
    
    @RequestMapping("/login")
    public String login() {
        return "login.jsp";
    }
    
    @RequestMapping(value="/registration", method=RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
        // if result has errors, return the registration page (don't worry about validations just now)
        // else, save the user in the database, save the user id in session, and redirect them to the /home route
    	userValidator.validate(user, result);
    	if(result.hasErrors()) {
//    		redirectAttributes.addFlashAttribute("error", "Invalid Credentials");
    		return "registration.jsp";
    	}else {
    		User newUser = userService.registerUser(user);
//    		putting in session Only the user id
    		session.setAttribute("user_id", newUser.getId());
    		return "redirect:/home";
    	}
    }
    
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session, RedirectAttributes redirectAttributes
 ) {
        // if the user is authenticated, save their user id in session
        // else, add error messages and return the login page
    	
    	
    	if(userService.authenticateUser(email, password)) {
    		Long iD = userService.findByEmail(email).getId();
    		session.setAttribute("user_id", iD);
    		return "redirect:/home";
    	}else {
//    		model.addAttribute("error", "Whatever");
    		redirectAttributes.addFlashAttribute("error", "Invalid Credentials");
    		return "redirect:/login";
    	}
    		
    	
    }
    
    @RequestMapping("/home")
    public String home(HttpSession session, Model model) {
        // get user from session, save them in the model and return the home page
    	Long id = (Long) session.getAttribute("user_id");
    	if(id != null) {
    		User thisUser = userService.findUserById(id);
    		model.addAttribute("user",thisUser);
    		return "homePage.jsp";
    	}
    	return "redirect:/login";
    }
    
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        // invalidate session
    	session.invalidate();
        // redirect to login page
    	return "redirect:/login";
    }
}


