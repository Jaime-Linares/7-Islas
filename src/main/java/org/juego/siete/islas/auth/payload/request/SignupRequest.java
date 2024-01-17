package org.juego.siete.islas.auth.payload.request;

import java.time.LocalDate;

import org.hibernate.annotations.ColumnDefault;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
	
	// User
	@NotBlank
	private String username;
	
	private String authority;

	@NotBlank
	private String password;
	
	//Both
	@NotBlank
	private String firstName;
	
	@NotBlank
	private String lastName;
	
	private LocalDate registrationDate;

	private LocalDate birthdayDate;

	@ColumnDefault("'Estandar'")
	private String image;

	@NotBlank
	private String email;

}
