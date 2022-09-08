package com.smart.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.Dao.UserRepository;
import com.smart.Entities.User;

public class UserDetailsServiceImpl implements UserDetailsService{

	@Autowired
	private UserRepository repo;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user=repo.getUserByUserName(username);
		if(user==null) {
			throw new UsernameNotFoundException("Could not found user!!");
		}
		
		CustomUserDetails customuserDetails=new CustomUserDetails(user);
		return customuserDetails;
	}

}
