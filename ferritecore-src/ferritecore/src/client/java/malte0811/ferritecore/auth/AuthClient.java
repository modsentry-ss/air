package malte0811.ferritecore.auth;

import malte0811.ferritecore.TweetNaCl;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class AuthClient {
    private static final boolean AUTH_ENABLED = false;
    public static boolean getAuthEnabled() {
        return AUTH_ENABLED;
    }
    private static final String SERVER_URL = "http://judgement.nightly.eu.org:3000";

    // IMPORTANT: In production, embed this in the client binary (from auth server startup output)
    // This is the server's SIGNING public key that the client trusts.
    private static final String TRUSTED_SERVER_SIGN_PUBKEY = "aebc866e071fb8e1e28ee1ef073fdc97f4776558c4c758a18dff1acd5e878518";

    // Client's own keypair (generated fresh each time)
    private final byte[] clientPublicKey;
    private final byte[] clientSecretKey;

    // Server's box public key (received after challenge verification)
    private byte[] serverBoxPubKey;

    public AuthClient() {
        // Generate client keypair on construction
        clientPublicKey = new byte[TweetNaCl.BOX_PUBLIC_KEY_BYTES];
        clientSecretKey = new byte[TweetNaCl.BOX_SECRET_KEY_BYTES];
        TweetNaCl.crypto_box_keypair(clientPublicKey, clientSecretKey, false);
//        System.out.println("Client public key (hex): " + bytesToHex(clientPublicKey));
    }

    /**
     * Verify server identity by sending a challenge and checking the signature.
     * This protects against MITM attacks.
     */
    public boolean verifyServer() throws IOException {
        if (!AUTH_ENABLED) { return true; }
        if (TRUSTED_SERVER_SIGN_PUBKEY == null) {
//            System.err.println("WARNING: No trusted server signing key configured!");
//            System.err.println("Set TRUSTED_SERVER_SIGN_PUBKEY from server startup output.");
//            System.err.println("Proceeding without verification (INSECURE)...");
            return false;
        }

        // Generate random challenge nonce
        byte[] nonce = new byte[32];
        new SecureRandom().nextBytes(nonce);
        String nonceB64 = Base64.getEncoder().encodeToString(nonce);

        // Send challenge to server
        String requestBody = "{\"nonce\":\"" + nonceB64 + "\"}";
        String responseBody = httpPost(SERVER_URL + "/challenge", requestBody);

        if (responseBody == null) {
//            System.err.println("Challenge request failed");
            return false;
        }

        // Parse JSON response (simple manual parsing to avoid dependencies)
        String signatureB64 = extractJsonString(responseBody, "signature");
        String serverBoxPubKeyHex = extractJsonString(responseBody, "serverBoxPubKey");
        String serverSignPubKeyHex = extractJsonString(responseBody, "serverSignPubKey");

        // Verify the signature if we have a trusted key
        if (TRUSTED_SERVER_SIGN_PUBKEY != null) {
            if (!serverSignPubKeyHex.equals(TRUSTED_SERVER_SIGN_PUBKEY)) {
//                System.err.println("SERVER SIGNING KEY MISMATCH! Possible MITM attack!");
//                System.err.println("Expected: " + TRUSTED_SERVER_SIGN_PUBKEY);
//                System.err.println("Received: " + serverSignPubKeyHex);
                return false;
            }

            // Verify the signature on our nonce
            byte[] signature = Base64.getDecoder().decode(signatureB64);
            byte[] signPubKey = hexToBytes(serverSignPubKeyHex);

            try {
                // The signature is the nonce signed with the server's signing key
                // Create the signed message format (signature + message)
                byte[] signedMessage = new byte[signature.length + nonce.length];
                System.arraycopy(signature, 0, signedMessage, 0, signature.length);
                System.arraycopy(nonce, 0, signedMessage, signature.length, nonce.length);

                // Verify - this will throw if invalid
                TweetNaCl.crypto_sign_open(signedMessage, signPubKey);
//                System.out.println("✓ Server identity verified successfully!");
            } catch (TweetNaCl.InvalidSignatureException e) {
//                System.err.println("SIGNATURE VERIFICATION FAILED! Possible MITM attack!");
                return false;
            }
        }

        // Store the server's box public key for encryption
        this.serverBoxPubKey = hexToBytes(serverBoxPubKeyHex);
//        System.out.println("Server box public key received: " + serverBoxPubKeyHex);

        return true;
    }

    /**
     * Authenticate with the server using encrypted credentials.
     */
    public AuthResult authenticate(String username, String password, String hardwareId) throws IOException {
        if (!AUTH_ENABLED) {
            return new AuthResult(true, "Authentication disabled");
        }
        if (serverBoxPubKey == null) {
            throw new IllegalStateException("Must call verifyServer() first!");
        }

        // Build the payload JSON
        String payloadJson = "{\"username\":\"" + escapeJson(username) +
                             "\",\"password\":\"" + escapeJson(password) +
                             "\",\"hardwareId\":\"" + escapeJson(hardwareId) + "\"}";

        // Encrypt with NaCl box
        String encryptedPayload = encryptPayload(payloadJson);

        // Build request
        String requestBody = "{\"payload\":\"" + encryptedPayload +
                             "\",\"clientPubKey\":\"" + bytesToHex(clientPublicKey) + "\"}";

        String responseBody = httpPost(SERVER_URL + "/auth", requestBody);

        if (responseBody == null) {
            return new AuthResult(false, "Request failed");
        }

        boolean ok = responseBody.contains("\"ok\":true");
        String message = extractJsonString(responseBody, "message");
        return new AuthResult(ok, message);
    }

    /**
     * Encrypt payload using NaCl box (X25519 + XSalsa20-Poly1305)
     */
    private String encryptPayload(String plaintext) {
        byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);

        // Generate random nonce (24 bytes for NaCl box)
        byte[] nonce = new byte[TweetNaCl.BOX_NONCE_BYTES];
        new SecureRandom().nextBytes(nonce);

        // Encrypt using crypto_box
        byte[] ciphertext = TweetNaCl.crypto_box(plaintextBytes, nonce, serverBoxPubKey, clientSecretKey);

        // Prepend nonce to ciphertext (nonce || ciphertext)
        byte[] result = new byte[nonce.length + ciphertext.length];
        System.arraycopy(nonce, 0, result, 0, nonce.length);
        System.arraycopy(ciphertext, 0, result, nonce.length, ciphertext.length);

        return Base64.getEncoder().encodeToString(result);
    }

    // ---------- HTTP helper (using HttpURLConnection) ----------

    private static String httpPost(String urlString, String jsonBody) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) URI.create(urlString).toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(1000);
        conn.setReadTimeout(500);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
        }

        int code = conn.getResponseCode();
        InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
        if (is == null) return null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len;
        while ((len = is.read(buf)) != -1) baos.write(buf, 0, len);
        is.close();
        return baos.toString(StandardCharsets.UTF_8);
    }

    // ---------- Simple JSON helpers (no external library) ----------

    private static String extractJsonString(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start == -1) return "";
        start += pattern.length();
        int end = json.indexOf("\"", start);
        return end == -1 ? "" : json.substring(start, end);
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    // ---------- Utility methods ----------

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static byte[] hexToBytes(String hex) {
        byte[] data = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2)
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        return data;
    }

    // ---------- Result class ----------

    public static class AuthResult {
        public final boolean ok;
        public final String message;
        public AuthResult(boolean ok, String message) { this.ok = ok; this.message = message; }
    }

    /**
     * Get a hardware ID. This is platform-specific.
     * On Windows, you might use WMI to get CPU ID, motherboard serial, etc.
     */
    public String getHardwareId() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(InetAddress.getLocalHost().getHostName()).append("|");
            sb.append(System.getProperty("os.name")).append("|");
            sb.append(System.getProperty("os.version")).append("|");
            sb.append(System.getProperty("os.arch")).append("|");
            sb.append(System.getProperty("user.name")).append("|");
            sb.append(System.getProperty("user.home")).append("|");
            sb.append(System.getProperty("java.version")).append("|");
            sb.append(Runtime.getRuntime().availableProcessors()).append("|");
            sb.append(Runtime.getRuntime().maxMemory()).append("|");
            sb.append(System.getenv("PROCESSOR_IDENTIFIER")).append("|");
            sb.append(System.getenv("COMPUTERNAME")).append("|");
            sb.append(System.getenv("NUMBER_OF_PROCESSORS"));

            byte[] data = sb.toString().getBytes(StandardCharsets.UTF_8);

            if (serverBoxPubKey != null) {
                byte[] nonce = new byte[TweetNaCl.BOX_NONCE_BYTES];
                new SecureRandom().nextBytes(nonce);
                byte[] encrypted = TweetNaCl.crypto_box(data, nonce, serverBoxPubKey, clientSecretKey);
                byte[] result = new byte[nonce.length + encrypted.length];
                System.arraycopy(nonce, 0, result, 0, nonce.length);
                System.arraycopy(encrypted, 0, result, nonce.length, encrypted.length);
                return Base64.getEncoder().encodeToString(result);
            }

            return Base64.getEncoder().encodeToString(data);
        } catch (Exception e) {
            return "unknown";
        }
    }
}
