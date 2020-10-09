package de.schoolprojects.geoTracker;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@PageTitle("Login")
public class LoginView extends HorizontalLayout {

    private ArrayList<OnLoginListener> loginListeners = new ArrayList<>();

    final int hashIterations = 10000;
    final int keyLengthInBit = 512;
    final int saltLengthInBytes = 32;

    UserEntry loginUser;

    public LoginView() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidth("100%");
        horizontalLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        add(horizontalLayout);
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
        }
//        System.out.println(DDef.pathToDatabase);


        LoginForm loginForm = new LoginForm();
        setSizeFull();
        loginForm.addLoginListener(e -> {
            boolean isAuthenticated = authenticate(e);
            if (isAuthenticated) {
                loginListeners.forEach(l -> l.onLogin(loginUser));
            } else {
                loginForm.setError(true);
            }
        });
        horizontalLayout.add(loginForm);

    }



    private boolean authenticate(AbstractLogin.LoginEvent event) {
//        String salt = getNewSalt();
//        System.out.println(salt + ":" + hash(event.getPassword(), salt));
        try {
            this.loginUser = DatabaseAccess.getUserByName(event.getUsername());

            String salt = loginUser.getPwdHash().split(":")[0];
            String hash = loginUser.getPwdHash().split(":")[1];

            if (hash(event.getPassword(), salt).equals(hash)) {
                return true;
            }
        } catch (NullPointerException e) {
            System.err.println(e);
        }
        return false;
    }

    private String hash(String password, String salt) {
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), hashIterations, keyLengthInBit);
            SecretKey key = secretKeyFactory.generateSecret(pbeKeySpec);
            byte[] hashedPwdByte = key.getEncoded();

            return Hex.encodeHexString(hashedPwdByte);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private String getNewSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] hashInBytes = new byte[saltLengthInBytes];
        secureRandom.nextBytes(hashInBytes);
        return Hex.encodeHexString(hashInBytes);
    }

    public void addLoginListener(OnLoginListener l) {
        loginListeners.add(l);
    }
}
