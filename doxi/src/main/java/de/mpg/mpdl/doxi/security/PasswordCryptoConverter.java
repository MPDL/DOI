package de.mpg.mpdl.doxi.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import org.mindrot.jbcrypt.BCrypt;

@Converter
public class PasswordCryptoConverter implements AttributeConverter<String, String> {

  @Override
  public String convertToDatabaseColumn(String plainPassword) {
    if (plainPassword != null && !plainPassword.startsWith("$2a$")) {
      // encrypt password
      String encryptedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
      return encryptedPassword;

    }
    
    return plainPassword;
  }

  @Override
  public String convertToEntityAttribute(String columnPassword) {
    return columnPassword;
  }
}
