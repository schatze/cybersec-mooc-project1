/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sec.project.config;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Osku
 */
public class CustomPasswordEncoder implements PasswordEncoder {
    
    MessageDigestPasswordEncoder enc = new Md5PasswordEncoder();
    
    @Override
    public String encode(CharSequence rawPassword) {
        String hashed = enc.encodePassword(rawPassword.toString(), null);   // bad hash and no salt! :(
        return hashed;
    }
    
    
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return enc.isPasswordValid(encodedPassword, rawPassword.toString(), null);
    }
    
}
