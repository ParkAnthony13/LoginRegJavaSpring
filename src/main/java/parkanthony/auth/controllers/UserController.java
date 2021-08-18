package parkanthony.auth.controllers;

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

import parkanthony.auth.models.User;
import parkanthony.auth.services.UserService;

//imports removed for brevity
@Controller
public class UserController {
	private final UserService userService;
 
	public UserController(UserService userService) {
	     this.userService = userService;
	}
	@RequestMapping("/")
	public String landingPage() {
		return "redirect:/registration";
	}
	@RequestMapping("/registration")
	public String registerForm(@ModelAttribute("user") User user) {
	    return "/auth/registrationPage.jsp";
	}
	@RequestMapping("/login")
	public String login() {
		return "/auth/loginPage.jsp";
	}
	 
	@RequestMapping(value="/registration", method=RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
	// if result has errors, return the registration page (don't worry about validations just now)
		if(result.hasErrors()) {
			return "redirect:/registration";
		} else {
			// else, save the user in the database, save the user id in session, and redirect them to the /home route
			Long id = userService.registerUser(user).getId();
			session.setAttribute("userSesh", id);
			return "redirect:/home";
		}
	}
	 
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session,RedirectAttributes redirectAttributes) {
	// if the user is authenticated, save their user id in session
		if(userService.authenticateUser(email, password)) {
			User user = userService.findByEmail(email);
			Long id = user.getId();
			session.setAttribute("userSesh", id);
			return "redirect:/home";
		} else {
			// else, add error messages and return the login page
			redirectAttributes.addFlashAttribute("errorLogin","Invalid Email/Password");
			return "redirect:/login";
		}
	}
	 
	@RequestMapping("/home")
	public String home(HttpSession session, Model model) {
	// if user not logged in
		if (session.getAttribute("userSesh")==null) {
			return "redirect:/login";
		}
	// get user from session, save them in the model and return the home page
		Long id = (Long) session.getAttribute("userSesh");
		User user = userService.findUserById(id);
		model.addAttribute("user", user);
		return "/auth/homePage.jsp";
	}
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
	// invalidate session
		session.invalidate();
	// redirect to login page
		return "redirect:/login";
	}
}
