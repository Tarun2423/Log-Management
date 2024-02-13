import java.io.IOException;
import java.net.PasswordAuthentication;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SendEmail extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        String email = req.getParameter("email");
        String subject = req.getParameter("sub");
        String to = req.getParameter("to");
        System.out.println("inn");
        System.out.println(email);
        System.out.println(to);
        System.out.println(subject);
        try{
            if(sendEmail(to, subject, email)){
                res.getWriter().println("success");
               }
               else{
                res.getWriter().println("failure");
               }
        }
         catch (IOException ex) {
        ex.printStackTrace(); // Print the stack trace for debugging
    }
    }

    private boolean sendEmail(String to, String subject, String body) {
        final String username = "taru19421.cs@rmkec.ac.in";
        final String password = "tarun@rmkec";

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(username, password);
            }
        });
        

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            System.out.println("Email sent successfully!");
            return true;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
       
    }
}
