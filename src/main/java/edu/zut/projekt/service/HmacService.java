package edu.zut.projekt.service;

import edu.zut.projekt.model.CreateReservationRequest;
import edu.zut.projekt.model.ReservationPayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Service
public class HmacService {

    @Value("${app.security.hmac-secret}")
    private String secret;
    private static final String ALGORITHM = "HmacSHA256";

    public boolean isValidReservationSignature(String providedSignature, CreateReservationRequest request) {
        String expectedSignature = calculateReservationSignature(request);
        return expectedSignature.equalsIgnoreCase(providedSignature);
    }

    public String calculateReservationSignature(CreateReservationRequest request) {
        ReservationPayload reservation = request.getReservation();

        String canonical = String.join("|",
                request.getRequestHeader().getRequestId().toString(),
                request.getRequestHeader().getSendDate().toString(),
                reservation.getRoomId().toString(),
                reservation.getMeetingTitle(),
                reservation.getDescription() == null ? "" : reservation.getDescription(),
                reservation.getStartDateTime().toString(),
                reservation.getEndDateTime().toString()
        );

        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8),
                    ALGORITHM
            );
            mac.init(keySpec);
            byte[] hash = mac.doFinal(canonical.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Bład podczas wyliczania podpisu HMAC.", e);
        }
    }
}