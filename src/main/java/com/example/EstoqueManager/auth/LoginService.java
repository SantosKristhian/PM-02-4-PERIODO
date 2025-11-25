//AuthenticationService.java
package com.example.EstoqueManager.auth;

import com.example.EstoqueManager.model.UsuarioModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.example.EstoqueManager.config.JwtServiceGenerator;

@Service
public class LoginService {

	@Autowired
	private LoginRepository repository;
	@Autowired
	private JwtServiceGenerator jwtService;
	@Autowired
	private AuthenticationManager authenticationManager;


	
	public String logar(LoginDTO loginDTO) {

		String token = this.gerarToken(loginDTO);
		return token;

	}



	public String gerarToken(LoginDTO loginDTO) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginDTO.getLogin(),
						loginDTO.getSenha()
						)
				);
		UsuarioModel user = repository.findByUsername(loginDTO.getLogin()).get();
		String jwtToken = jwtService.generateToken(user);
		return jwtToken;
	}


}
