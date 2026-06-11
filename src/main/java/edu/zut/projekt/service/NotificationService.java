package edu.zut.projekt.service;

import edu.zut.projekt.config.AppMailProperties;
import edu.zut.projekt.model.Reservation;
import edu.zut.projekt.model.Room;
import edu.zut.projekt.model.User;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;
    private final AppMailProperties appMailProperties;

    public NotificationService(JavaMailSender mailSender, AppMailProperties appMailProperties) {
        this.mailSender = mailSender;
        this.appMailProperties = appMailProperties;
    }

    public void sendReservationConfirmation(User user, Reservation reservation, Room room) {
        System.out.println("=== MAIL DEBUG START ===");
        System.out.println("Mail enabled: " + appMailProperties.isEnabled());
        System.out.println("Mail from: " + appMailProperties.getFrom());
        System.out.println("Mail to: " + user.getEmail());
        System.out.println("Reservation id: " + reservation.getId());

        if (!appMailProperties.isEnabled()) {
            System.out.println("Wysylka maili jest wylaczona.");
            System.out.println("=== MAIL DEBUG END ===");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setFrom(appMailProperties.getFrom());
            helper.setTo(user.getEmail());
            helper.setSubject("Potwierdzenie rezerwacji sali konferencyjnej");

            String html = buildReservationConfirmationHtml(user, reservation, room);
            helper.setText(html, true);

            System.out.println("Proba wysylki maila...");
            mailSender.send(message);
            System.out.println("Mail wyslany poprawnie.");
        } catch (Exception e) {
            System.out.println("BLAD przy wysylce maila: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=== MAIL DEBUG END ===");
    }

    private String buildReservationConfirmationHtml(User user, Reservation reservation, Room room) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        String start = reservation.getStartDateTime().format(formatter);
        String end = reservation.getEndDateTime().format(formatter);

        return """
        <!DOCTYPE html>
        <html lang="pl">
        <head>
            <meta charset="UTF-8">
            <title>Potwierdzenie rezerwacji</title>
        </head>
        <body style="margin:0;padding:0;background-color:#f4f6f8;font-family:Arial,sans-serif;color:#1f2937;">
            <div style="max-width:700px;margin:30px auto;background:#ffffff;border:1px solid #e5e7eb;border-radius:12px;overflow:hidden;">
                <div style="background:#1f4f8a;color:#ffffff;padding:24px 32px;">
                    <h1 style="margin:0;font-size:24px;">Potwierdzenie rezerwacji</h1>
                    <p style="margin:8px 0 0 0;font-size:14px;opacity:0.95;">
                        Rezerwacja sali konferencyjnej zostala poprawnie utworzona.
                    </p>
                </div>

                <div style="padding:32px;">
                    <p style="margin-top:0;">Dzien dobry <strong>%s %s</strong>,</p>

                    <p>
                        potwierdzamy utworzenie rezerwacji w systemie rezerwacji sal konferencyjnych.
                    </p>

                    <div style="margin:24px 0;padding:20px;background:#f9fafb;border:1px solid #e5e7eb;border-radius:10px;">
                        <h2 style="margin:0 0 16px 0;font-size:18px;color:#111827;">Szczegoly rezerwacji</h2>

                        <p style="margin:8px 0;"><strong>ID rezerwacji:</strong> %s</p>
                        <p style="margin:8px 0;"><strong>Tytul spotkania:</strong> %s</p>
                        <p style="margin:8px 0;"><strong>Opis:</strong> %s</p>
                        <p style="margin:8px 0;"><strong>Sala:</strong> %s</p>
                        <p style="margin:8px 0;"><strong>Lokalizacja:</strong> %s</p>
                        <p style="margin:8px 0;"><strong>Pietro:</strong> %s</p>
                        <p style="margin:8px 0;"><strong>Poczatek:</strong> %s</p>
                        <p style="margin:8px 0;"><strong>Koniec:</strong> %s</p>
                        <p style="margin:8px 0;"><strong>Status:</strong> %s</p>
                    </div>

                    <p style="margin-bottom:0;">
                        Wiadomosc zostala wygenerowana automatycznie. Prosimy na nia nie odpowiadac.
                    </p>
                </div>

                <div style="padding:16px 32px;background:#f9fafb;border-top:1px solid #e5e7eb;font-size:12px;color:#6b7280;">
                    System rezerwacji sal konferencyjnych · YourFurniture
                </div>
            </div>
        </body>
        </html>
        """.formatted(
                safe(user.getFirstName()),
                safe(user.getLastName()),
                reservation.getId(),
                safe(reservation.getMeetingTitle()),
                safe(reservation.getDescription()),
                safe(room.getName()),
                safe(room.getLocation()),
                room.getFloor(),
                start,
                end,
                reservation.getStatus()
        );
    }

    private String safe(String value) {
        return value == null ? "-" : value;
    }
}