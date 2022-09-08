package com.smart.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.Dao.ContactRepository;
import com.smart.Dao.UserRepository;
import com.smart.Entities.Contact;
import com.smart.Entities.User;

@RestController
public class SearchController {
	
	@Autowired
	private UserRepository userrepo;
	
	@Autowired
	private ContactRepository contactrepo;
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal){
		
		System.out.println(query);
		String name=principal.getName();
		User user=this.userrepo.getUserByUserName(name);
		List<Contact> contacts=this.contactrepo.findByNameContainingAndUser(query,user);
		return ResponseEntity.ok(contacts);
	}

}
