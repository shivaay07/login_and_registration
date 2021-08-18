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

import com.duttech.authentication.models.User;
import com.duttech.authentication.services.UserService;

@Controller
public class UserController {
	private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
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
    	System.out.println(user.getPassword());
    	System.out.println(user.getPasswordConfirmation());
    	String pass = user.getPassword();
    	String checkPass = user.getPasswordConfirmation();
    	if(!pass.equals(checkPass)) {
    		System.out.println(user.getPassword());
    		return "redirect:/registration";
    	}
    	if(result.hasErrors()) {
    		return "registrationPage.jsp";
    	}else {
    		User newUser = userService.registerUser(user);
//    		putting in session Only the user id
    		session.setAttribute("user_id", newUser.getId());
    		return "redirect:/home";
    	}
    }
    
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session) {
        // if the user is authenticated, save their user id in session
        // else, add error messages and return the login page
    	
    	
    	if(userService.authenticateUser(email, password)) {
    		Long iD = userService.findByEmail(email).getId();
    		session.setAttribute("user_id", iD);
    		return "redirect:/home";
    	}else {
    		model.addAttribute("error", "Whatever");
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


