package br.com.LuizMiguel.firstProject.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var autorization = request.getHeader("Authorization");

        var authEncoded = autorization.substring("Basic".length()).trim();

        byte[] authDecode = Base64.getDecoder().decode(authEncoded);

        var authString = new String(authDecode);


        String[] credentials = authString.split(":");
        String username = credentials [0];
        String password = credentials [1];
        System.out.println("Authorization");
        System.out.println(username);
        System.out.println(password);

    }



}
