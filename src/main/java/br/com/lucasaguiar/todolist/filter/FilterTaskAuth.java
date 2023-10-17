package br.com.lucasaguiar.todolist.filter;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.JsonSerializable.Base;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.lucasaguiar.todolist.user.IUserRepository;
import br.com.lucasaguiar.todolist.user.UserModel;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    IUserRepository userRepository;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException{
       
       var servletPath = request.getServletPath();

        if(servletPath.equals("/tasks/ListaDeTarefas")) {
            var authorization = request.getHeader("Authorization");
            //System.out.println("" + authorization);

            var authEncoded = authorization.substring("Basic".length()).trim();
            //System.out.println("" + authEncoded);

            byte[] authDecode = Base64.getDecoder().decode(authEncoded);
            //System.out.println("" + authDecode);

            var authString = new String(authDecode);
            //System.out.println("" + authString);

            String[] credentials = authString.split(":");
            String username = credentials[0];
            String password = credentials[1];

            // Validar usuario
            var user = this.userRepository.findByUsername(username);
            if (user == null) {
                response.sendError(401, "Usuário sem autorização");
            } else {
                //valida senha
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerify.verified) {
                    request.setAttribute("idUser", user.getId());
                    chain.doFilter(request, response);
                } else {
                    response.sendError(401);
                }
            }
        }

       if(servletPath.equals("/tasks/create")){
            var authorization = request.getHeader("Authorization");
                    //System.out.println("" + authorization);

                    var authEncoded = authorization.substring("Basic".length()).trim();
                    //System.out.println("" + authEncoded);

                    byte[] authDecode = Base64.getDecoder().decode(authEncoded);
                    //System.out.println("" + authDecode);

                    var authString = new String(authDecode);
                    //System.out.println("" + authString);
                    
                    String[] credentials = authString.split(":");
                    String username = credentials[0];
                    String password = credentials[1];

                    // Validar usuario
                    var user = this.userRepository.findByUsername(username);
                    if(user == null){
                        response.sendError(401, "Usuário sem autorização");
                    } else {
                        //valida senha
                    var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                    if(passwordVerify.verified){
                        request.setAttribute("idUser", user.getId());
                        chain.doFilter(request, response);
                    } else {
                            response.sendError(401);
                    }
                    }
            } else {
           chain.doFilter(request, response);
       }
    }
}
