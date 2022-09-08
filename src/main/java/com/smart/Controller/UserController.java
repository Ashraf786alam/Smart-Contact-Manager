package com.smart.Controller;


import com.razorpay.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.Dao.ContactRepository;
import com.smart.Dao.MyOrderRepository;
import com.smart.Dao.UserRepository;
import com.smart.Entities.Contact;
import com.smart.Entities.MyOrder;
import com.smart.Entities.User;
import com.smart.helper.Message;



@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bcryptpasswordencoder;
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private ContactRepository contactrepo;
	
	@Autowired
	private MyOrderRepository myorderRepo;
	
	@ModelAttribute
	public void CommonData(Model model, Principal principal) {
		
		String username=principal.getName();
		User user=repo.getUserByUserName(username);
		
		model.addAttribute("user",user);
		
		
	}
	@RequestMapping("/dashboard")
	public String dashboard(Model model,Principal principal) {
		
		model.addAttribute("title", "Dashboard-Smart Contact Manager");
		return "normal/dashboard";
		
	}
	
	@GetMapping("/add-contact")
	public String AddContact(Model model) {
		
		model.addAttribute("contact", new Contact());
		model.addAttribute("title", "Add-Contact-Smart Contact Manager");
		return "normal/add-contact";
		
	}
	
	@PostMapping("/process-contact")
	public String processcontact(@ModelAttribute("contact") Contact contact,
			@RequestParam("profile") MultipartFile file ,HttpSession session,
			Principal principal) {
		

     try {
    	
    	 System.out.println("Request aa rha haii..Add Contact page se");
    	 String name=principal.getName();
         User user=repo.getUserByUserName(name);
         
         //processing and uploading file...
         
         if(file.isEmpty()) {
        	 
        	 System.out.println("File Is Empty");
        	 contact.setImage("Default.png");
        	 
         }
         else {
        	 
        	 
        	 contact.setImage(file.getOriginalFilename());
        	 
        	 File savefile=new ClassPathResource("static/img").getFile();
        	 
        	 Path path=Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
        	 Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING );
        	 
        	 session.setAttribute("message",new Message("Your Contact is Added!! Add More","success"));
        	 
        	 
        	 System.out.println("Image is uploaded");
        	 
        	 
         }
         contact.setUser(user);
         user.getContact().add(contact);
         this.repo.save(user);
         
         System.out.println("Contact Added successfully");
     }
     
     catch(Exception e) {
    	 e.printStackTrace();
    	 session.setAttribute("message",new Message("Something went wrong..Try Again","danger"));
    	 
     }
		
		return "normal/add-contact";
	}
	
	
	@GetMapping("/show-contact/{page}")
	public String showContacts(@PathVariable("page") int page, Model model,Principal principal) {
		
     	String name=principal.getName();
	    User user=repo.getUserByUserName(name);
	    
	    Pageable pageable=PageRequest.of(page, 5);
	    
	    Page<Contact> contacts=contactrepo.findContactsByUser(user.getId(),pageable);
		
		model.addAttribute("title","View-Contact Page");
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentpage",page);
		model.addAttribute("totalpages",contacts.getTotalPages());
		
		return "normal/show-contact";
		
	}
	
	@GetMapping("/contact/{id}")
	public String showContact(@PathVariable("id") int id, Model model,Principal principal) {
		
		try {
			
			Optional<Contact> optional=this.contactrepo.findById(id);
			Contact contact=optional.get();
			
			String name=principal.getName();
			User user=repo.getUserByUserName(name);
			if(user.getId()==contact.getUser().getId()) {
			model.addAttribute("contact",contact);
			model.addAttribute("title",contact.getName());
			}
			
		}
		catch(Exception e) {
			
			e.printStackTrace();
			return "normal/contact-not-found.html";
			
		}
		return "normal/contact-detail";
	}
	
	
	@GetMapping("/delete/{id}")
	public String deleteContact(@PathVariable("id") int id,Principal principal,HttpSession session) {
		
		
		String name=principal.getName();
		User user=this.repo.getUserByUserName(name);
		
		Optional<Contact> optional=this.contactrepo.findById(id);
		Contact contact=optional.get();		
		if(user.getId()==contact.getUser().getId())
		{this.contactrepo.deleteById(id);
		 
		session.setAttribute("message",new Message("Contact Deleted Successfully","success"));
		
		}
		
		return "redirect:/user/show-contact/0";
		
	}
	
	@PostMapping("/update/{id}")
	public String UpdateForm(@PathVariable("id") int id,Model model) {
		
		Contact contact=this.contactrepo.findById(id).get();
		model.addAttribute("title","Update Form");
		model.addAttribute("contact",contact);
		return "normal/update-form";
	}
	
	
	@RequestMapping(value="/process-update" ,method=RequestMethod.POST)
	public String updateInDB(@ModelAttribute Contact contact, Principal principal, @RequestParam("cid") int cid, @RequestParam("profile") MultipartFile file,Model model,HttpSession session) {
		
		try {
			
			Contact oldcontact=this.contactrepo.findById(cid).get();
			
			if(!file.isEmpty()) {
				
				// delete old photo...
				
				File deleteFile=new ClassPathResource("static/img").getFile();
				File file1=new File(deleteFile,oldcontact.getImage());
				file1.delete();
				
				// update new photo
				
				File saveFile=new ClassPathResource("static/img").getFile();
				
				Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
				
				contact.setImage(file.getOriginalFilename());
			}
			else {
				
				contact.setImage(oldcontact.getImage());
			}
			
			session.setAttribute("message",new Message("Your Contact is Updated Successfully","success"));
			String name=principal.getName();
			
			User user=this.repo.getUserByUserName(name);
			contact.setUser(user);
			this.contactrepo.save(contact);
		}
		
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/user/contact/"+cid;
		
	}
	
	@GetMapping("/profile")
	public String Profile(Model model,Principal principal) {
		
		return "normal/profile";
	}
	
	@GetMapping("/setting")
	public String openSettings() {
		
		
		return "normal/setting";
	}
	
	@PostMapping("/change-password")
	public String changepassword(@RequestParam("oldpassword") String oldpassword, @RequestParam("newpassword") String newpassword,Principal principal,HttpSession session) {
		
		String name=principal.getName();
		User currentuser=this.repo.getUserByUserName(name);
		
		if(this.bcryptpasswordencoder.matches(oldpassword, currentuser.getPassword())) {
			
			currentuser.setPassword(this.bcryptpasswordencoder.encode(newpassword));
			this.repo.save(currentuser);
			session.setAttribute("message",new Message("Your password is changed Successfully","success"));
			
			
		}
		else {
			
			session.setAttribute("message",new Message("Please Enter correct old password","danger"));
			return "redirect:/user/setting";
		}
		
		return "redirect:/user/dashboard";
	}
	
	//   ------------------------------Forget Module------------------------------
	
	
	@PostMapping("/create_order")
	@ResponseBody
	public String CreateOrder(@RequestBody Map<String,Object> data,Principal principal) throws RazorpayException {
		
		System.out.println("Razorpay Integeration");
		int amount=Integer.parseInt(data.get("amount").toString());
		 
		RazorpayClient client=new RazorpayClient("rzp_test_iQaJNXH48UDbcP","D1LseYteeBcp5eQSKpMB6VhP");
		
		JSONObject ob=new JSONObject();
		ob.put("amount",amount*100);
		ob.put("currency", "INR");
		
		Order order=client.Orders.create(ob);
		
		MyOrder myorder=new MyOrder();
		myorder.setAmount(order.get("amount"));
		myorder.setOrderId(order.get("id"));
		myorder.setPaymentId(null);
		myorder.setStatus(order.get("status"));
		String email=principal.getName();
		User user=this.repo.getUserByUserName(email);
		myorder.setUser(user);
		this.myorderRepo.save(myorder);
		System.out.println(order);
		return order.toString();
		
	}
	
	@PostMapping("/update-order")
	public ResponseEntity<?> updateOrder(@RequestBody Map<String,Object> data){
		System.out.println("DATA="+data);
		System.out.println("-------------------@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@---------------------");
		MyOrder myorder=this.myorderRepo.findByOrderId(data.get("order_id").toString());
		myorder.setPaymentId(data.get("payment_id").toString());
		myorder.setStatus(data.get("status").toString());
		this.myorderRepo.save(myorder);
		
		return ResponseEntity.ok(Map.of("msg","Updated"));
	}

}
