package com.eventsphere.booking;

import com.eventsphere.booking.dto.BookingResponse;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class EmailService {

    private static final Logger log =
            LoggerFactory.getLogger(EmailService.class);

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy",
                    Locale.ENGLISH
            );

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern(
                    "h:mm a",
                    Locale.ENGLISH
            );

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String senderEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendBookingConfirmation(
            String recipientEmail,
            BookingResponse booking) {

        if (recipientEmail == null
                || recipientEmail.isBlank()) {

            log.warn(
                    "Booking {} has no recipient email; confirmation email skipped",
                    booking.bookingReference()
            );

            return;
        }

        /*
         * Sender email must be configured.
         */
        if (senderEmail == null
                || senderEmail.isBlank()) {

            log.error(
                    "MAIL_USERNAME is not configured. " +
                            "Booking {} was saved but email was not sent.",
                    booking.bookingReference()
            );

            return;
        }

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            /*
             * true = multipart capable
             * UTF-8 = supports Unicode characters
             */
            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );

            helper.setFrom(senderEmail);

            helper.setTo(recipientEmail);

            helper.setSubject(
                    "EventSphere Booking Confirmation - "
                            + booking.bookingReference()
            );

            helper.setText(
                    buildHtml(booking),
                    true
            );

            mailSender.send(message);

            log.info(
                    "Booking confirmation {} sent successfully to {}",
                    booking.bookingReference(),
                    recipientEmail
            );

        } catch (MessagingException
                 | RuntimeException ex) {

            /*
             * IMPORTANT:
             *
             * Booking is already saved in MongoDB.
             * Email failure must not turn a successful booking
             * into a failed booking.
             */
            log.error(
                    "Booking {} was saved, but confirmation email to {} failed: {}",
                    booking.bookingReference(),
                    recipientEmail,
                    ex.getMessage(),
                    ex
            );
        }
    }

    private String buildHtml(
            BookingResponse booking) {

        String eventDate =
                booking.eventDate() == null
                        ? "-"
                        : booking.eventDate()
                        .format(DATE_FORMAT);

        String eventTime =
                booking.eventTime() == null
                        ? "-"
                        : booking.eventTime()
                        .format(TIME_FORMAT);

        String totalPrice =
                booking.totalPrice() == null
                        ? "0.00"
                        : booking.totalPrice()
                        .toPlainString();

        int ticketCount =
                booking.ticketCount() == null
                        ? 0
                        : booking.ticketCount();

        return """
                <!DOCTYPE html>
                <html lang="en">

                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport"
                          content="width=device-width, initial-scale=1.0">

                    <title>
                        EventSphere Booking Confirmation
                    </title>
                </head>

                <body style="
                    margin:0;
                    padding:0;
                    background:#f4f4f2;
                    font-family:Arial,Helvetica,sans-serif;
                    color:#0a0a0a;
                ">

                <div style="
                    max-width:680px;
                    margin:30px auto;
                    background:#ffffff;
                    border:2px solid #0a0a0a;
                ">

                    <!-- HEADER -->

                    <div style="
                        background:#0a0a0a;
                        color:#ffffff;
                        padding:26px 30px;
                    ">

                        <h1 style="
                            margin:0;
                            font-size:28px;
                        ">
                            EventSphere
                        </h1>

                        <p style="
                            margin:8px 0 0;
                            color:#d0d0d0;
                        ">
                            Booking confirmation
                        </p>

                    </div>


                    <!-- BODY -->

                    <div style="
                        padding:30px;
                    ">

                        <h2 style="
                            margin-top:0;
                        ">
                            Your ticket is confirmed 🎟
                        </h2>

                        <p style="
                            line-height:1.6;
                        ">
                            Hi %s,
                            your EventSphere booking has been
                            successfully confirmed.
                        </p>


                        <!-- BOOKING REFERENCE -->

                        <div style="
                            margin:24px 0;
                            padding:20px;
                            border:2px dashed #0a0a0a;
                        ">

                            <p style="
                                margin:0 0 8px;
                                color:#666666;
                                font-size:12px;
                                text-transform:uppercase;
                                letter-spacing:1px;
                            ">
                                Booking reference
                            </p>

                            <div style="
                                font-size:24px;
                                font-weight:bold;
                                letter-spacing:2px;
                            ">
                                %s
                            </div>

                        </div>


                        <!-- BOOKING DETAILS -->

                        <table style="
                            width:100%%;
                            border-collapse:collapse;
                        ">

                            <tr>
                                <td style="
                                    padding:9px 0;
                                    font-weight:bold;
                                ">
                                    Event
                                </td>

                                <td style="
                                    padding:9px 0;
                                ">
                                    %s
                                </td>
                            </tr>


                            <tr>
                                <td style="
                                    padding:9px 0;
                                    font-weight:bold;
                                ">
                                    Venue
                                </td>

                                <td style="
                                    padding:9px 0;
                                ">
                                    %s
                                </td>
                            </tr>


                            <tr>
                                <td style="
                                    padding:9px 0;
                                    font-weight:bold;
                                ">
                                    Date
                                </td>

                                <td style="
                                    padding:9px 0;
                                ">
                                    %s
                                </td>
                            </tr>


                            <tr>
                                <td style="
                                    padding:9px 0;
                                    font-weight:bold;
                                ">
                                    Time
                                </td>

                                <td style="
                                    padding:9px 0;
                                ">
                                    %s
                                </td>
                            </tr>


                            <tr>
                                <td style="
                                    padding:9px 0;
                                    font-weight:bold;
                                ">
                                    Tickets
                                </td>

                                <td style="
                                    padding:9px 0;
                                ">
                                    %d
                                </td>
                            </tr>


                            <tr>
                                <td style="
                                    padding:9px 0;
                                    font-weight:bold;
                                ">
                                    Total
                                </td>

                                <td style="
                                    padding:9px 0;
                                    font-weight:bold;
                                ">
                                    $%s
                                </td>
                            </tr>


                            <tr>
                                <td style="
                                    padding:9px 0;
                                    font-weight:bold;
                                ">
                                    Status
                                </td>

                                <td style="
                                    padding:9px 0;
                                ">
                                    %s
                                </td>
                            </tr>

                        </table>


                        <p style="
                            margin-top:28px;
                            color:#666666;
                            font-size:13px;
                            line-height:1.5;
                        ">
                            Please keep this email and your booking
                            reference for the event.
                        </p>

                    </div>


                    <!-- FOOTER -->

                    <div style="
                        padding:18px 30px;
                        border-top:1px solid #dddddd;
                        color:#777777;
                        font-size:12px;
                    ">

                        EventSphere · Automated booking notification

                    </div>

                </div>

                </body>
                </html>
                """.formatted(
                safe(booking.userFullName()),
                safe(booking.bookingReference()),
                safe(booking.eventTitle()),
                safe(booking.eventVenue()),
                safe(eventDate),
                safe(eventTime),
                ticketCount,
                safe(totalPrice),
                safe(booking.status())
        );
    }

    private String safe(String value) {

        if (value == null
                || value.isBlank()) {

            return "-";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}