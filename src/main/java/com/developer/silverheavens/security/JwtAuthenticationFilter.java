package com.developer.silverheavens.security;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.developer.silverheavens.util.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	@Autowired
	JwtUtil jwtUtil;
	
	@Autowired
	SilverHeavensUserDetailsService userDetailsService;
	
	private static final Logger logger =  LogManager.getLogger("MyLoggerOne");

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		System.out.println("A-"+SecurityContextHolder.getContext().getAuthentication());
		
		logger.info("JWT filter invoked.");
  
		String requestTokenHeader = request.getHeader("Authorization");
		String username=null;
		String jwtToken=null;
		
		

		//null and format
		if(requestTokenHeader!=null && requestTokenHeader.startsWith("Bearer ")){
			jwtToken=requestTokenHeader.substring(7);
			logger.info("JWT token : "+jwtToken);
			//validate token

			try{
				username = jwtUtil.getUsernameFromToken(jwtToken);
            }catch(ExpiredJwtException e) {
            	response.addHeader("reason", "JWT Expired");
            }
			catch (Exception e){
            	logger.info("JWT Exception : "+e);
            	filterChain.doFilter(request,response);
                //e.printStackTrace();
            }

            if(username!=null 
            		&& SecurityContextHolder.getContext().getAuthentication()==null 
            		&& jwtUtil.validateToken(jwtToken, username)){
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }else{
            	logger.info("Token is not validated.");
            }
		}
		System.out.println("B-"+SecurityContextHolder.getContext().getAuthentication());
        filterChain.doFilter(request,response);		
	}

}
