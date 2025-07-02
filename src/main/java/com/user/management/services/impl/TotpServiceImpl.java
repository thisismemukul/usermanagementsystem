package com.user.management.services.impl;

import com.user.management.services.TotpService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.stereotype.Service;

@Service
public class TotpServiceImpl implements TotpService {

    private final GoogleAuthenticator gAuth;

    public TotpServiceImpl() {
        this.gAuth = new GoogleAuthenticator();
    }
    public TotpServiceImpl(GoogleAuthenticator gAuth) {
        this.gAuth = gAuth;
    }

    @Override
    public GoogleAuthenticatorKey generateSecretKey() {
        return gAuth.createCredentials();
    }

    @Override
    public String getQRCodeUrl(GoogleAuthenticatorKey secret, String username) {
        return GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("UserMgmt", username, secret);
    }

    @Override
    public boolean verifyCode(String secret, int code) {
        return gAuth.authorize(secret, code);
    }
}
