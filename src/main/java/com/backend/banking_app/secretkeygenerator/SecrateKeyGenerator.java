package com.backend.banking_app.secretkeygenerator;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
public class SecrateKeyGenerator {
   public  static void main(String[] args) {
       System.out.println("Generating secret key...");
SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
String secretString = Encoders.BASE64.encode(key.getEncoded());
System.out.println("Secret key: " + secretString);
}
}