package com.user.management.services.impl;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import static com.user.management.enums.ResponseCode.TWO_FA_SECRET_EMPTY;
import static com.user.management.util.UserManagementUtils.createUserMgmtException;

@Service
public class TotpService implements com.user.management.services.ITotpService {

    private final GoogleAuthenticator gAuth;

    public TotpService() {
        this.gAuth = new GoogleAuthenticator();
    }
    public TotpService(GoogleAuthenticator gAuth) {
        this.gAuth = gAuth;
    }

    @Override
    public GoogleAuthenticatorKey generateSecretKey() {
        return gAuth.createCredentials();
    }

    @Override
    public String getQRCodeUrl(GoogleAuthenticatorKey secret, String username) {
        if (ObjectUtils.isEmpty(secret)) {
            throw createUserMgmtException(TWO_FA_SECRET_EMPTY);
        }
        return GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("UserMgmt", username, secret);
    }

    @Override
    public boolean verifyCode(String secret, int code) {
        if (Strings.isEmpty(secret)) {
          throw createUserMgmtException(TWO_FA_SECRET_EMPTY);
        }
        return gAuth.authorize(secret, code);
    }
}
