package com.smart.Controller;

import java.util.Random;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.Dao.UserRepository;
import com.smart.Entities.User;
import com.smart.Service.EmailService;
import com.smart.helper.Message;

@Controller
public class HomeController {
	
	
	@Autowired
	private EmailService emailService;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepo;
	
	@RequestMapping("/")
	public String home(Model model) {
		
		model.addAttribute("name", "ashraf");
		model.addAttribute("home", "Hyd");
		model.addAttribute("title","Home-Smart Contact Manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		
		model.addAttribute("title","About-Smart Contact Manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model) {
		
		model.addAttribute("title","Register-Smart Contact Manager");
		model.addAttribute("user",new User());
		return "signup";
	}
	
	@RequestMapping(value="/do_register",method=RequestMethod.POST)
	public String fetchingdatafromSignuppage(@Valid @ModelAttribute("user") User user, BindingResult result,Model model, @RequestParam(value="agreement",defaultValue="false") boolean agreement ,HttpSession session) {
		
		try {
			
			if(!agreement) {
				
				throw new Exception(" You have not agreed the terms and conditions");
			}
			
			if(result.hasErrors()) {
				
				model.addAttribute("user",user);
				System.out.println("Error"+result.toString());
				return "signup";
				
			}
			
			user.setEnabled(true);
			user.setRole("ROLE_USER");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			
			User result1=this.userRepo.save(user);
			model.addAttribute("user",new User());
			session.setAttribute("message", new Message("Successfully Registerd","alert-success"));
			return "signup";
		}
		catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message", new Message(" Something Went Wrong!!"+e.getMessage(),"alert-danger"));
			return "signup";
		}
		
		
	}
	
	@GetMapping("/signin")
	public String CustomLogin(Model model) {
		
		model.addAttribute("title","Login-Smart Contact Manager");		
		return "login";
	}
	
	@GetMapping("/forget")
	public String openEmailForm() {
		
		return "normal/forget-email-form";
		
	}
	
	@PostMapping("/sendotp")
	public String SendOTP(@RequestParam("email") String email,HttpSession session) {
		
		//genrating OTP of 4 digits....
		Random random=new Random(10000);
		int otp=random.nextInt(999999);
		System.out.println("OTP="+otp);
		
		//send email 
		
		String subject="OTP From SCM";
		String message=""+
		                "<div style='border:1px solid green; padding:20px'>"
				        +"<h1>"
		                +"OTP is  "
				        +"<b>"+otp
				        +"<n>"
				        +"</h1>"
				        +"</div>";
		                		
		String to=email;
		boolean flag=emailService.sendEmail(subject, message, to);
		
		if(flag) {
			
			session.setAttribute("myotp",otp);
			session.setAttribute("email",email);
			return "normal/verify-otp";
		}
		else {
		    
			session.setAttribute("message","OTP couldn't to sent Your Email Id..");
			
			return "normal/forget-email-form";
		}
		
		
		
	}
	@PostMapping("/VerifyOTP")
	public String VerifyOTP(@RequestParam("otp") int otp,HttpSession session) {
		
		int myotp=(int)session.getAttribute("myotp");
		String email=(String)session.getAttribute("email");
		if(myotp==otp) {
			
			User user=this.userRepo.getUserByUserName(email);
			if(user==null) {
				session.setAttribute("message","No User with email "+email);
				return "normal/forget-email-form";
			}
			else {
				
				
				
				return "normal/password_change_form";
			}
			
			
		}
		else {
			
			session.setAttribute("message","You have entered wrong OTP");
			return "normal/verify-otp";
		}
		
	}
	
	@PostMapping("/changeTo-newpassword")
	public String ChangeToNewPassword(@RequestParam("newpassword") String newpassword,HttpSession session) {
		
		String email=(String) session.getAttribute("email");
		User user=this.userRepo.getUserByUserName(email);
		user.setPassword(this.passwordEncoder.encode(newpassword));
		this.userRepo.save(user);
		//session.setAttribute("message","");
		return "redirect:/signin?change=Password Changed Successfully ";
	}

}
