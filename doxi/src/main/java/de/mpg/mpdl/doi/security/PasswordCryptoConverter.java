package de.mpg.mpdl.doi.security;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.mindrot.jbcrypt.BCrypt;

@Converter
public class PasswordCryptoConverter implements AttributeConverter<String, String> {

	@Override
	public String convertToDatabaseColumn(String plainPassword) {
		
		if(plainPassword!=null)
		{
			String encryptedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
			return encryptedPassword;
			
		}
		return null;
		
	}

	@Override
	public String convertToEntityAttribute(String columnPassword) {
		
		return columnPassword;
	}

}
