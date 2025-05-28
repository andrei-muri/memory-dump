package muri.memdumpbackend.util;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@Getter
public class KeyUtil {
    private RSAPrivateKey privateKey;
    private RSAPublicKey  publicKey;

    @PostConstruct
    private void loadKeys() throws Exception {
        byte[] priv = Files.readAllBytes(Paths.get("src/main/resources/keys/private_key.pem"));
        privateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(stripPem(priv)));

        byte[] pub = Files.readAllBytes(Paths.get("src/main/resources/keys/public_key.pem"));
        publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(stripPem(pub)));
    }

    private byte[] stripPem(byte[] keyBytes) {
        String pem = new String(keyBytes)
                .replaceAll("-----\\w+ PRIVATE KEY-----", "")
                .replaceAll("-----\\w+ PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        return Base64.getDecoder().decode(pem);
    }
}

