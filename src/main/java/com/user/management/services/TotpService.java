package com.user.management.services;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

public interface TotpService {
    GoogleAuthenticatorKey generateSecretKey();

    String getQRCodeUrl(GoogleAuthenticatorKey secret, String username);

    boolean verifyCode(String secret, int code);
}
