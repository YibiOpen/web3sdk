package org.bcos.plugins;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bcos.web3j.protocol.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class EmailUtils {

    private static final Logger LOG = LoggerFactory.getLogger(EmailUtils.class);
    public static final ObjectMapper mapper = ObjectMapperFactory.getObjectMapper();

    @SuppressWarnings("restriction")
    public static boolean sslSend(MailMessage mailMessage) {
        String host = mailMessage.getHost();
        String port = mailMessage.getPort();
        String sendAccount = mailMessage.getSendAccount();
        String sendPwd = mailMessage.getSendPwd();
        String sendName = mailMessage.getSendName();
        String subject = mailMessage.getSubject();
        String content = mailMessage.getContent();
        String[] tos = mailMessage.getTo();

        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        // 设置邮件会话参数
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", host);
        props.setProperty("mail.smtp.port", port);
        if (!"25".equals(port)) {
            props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.socketFactory.port", port);
        }
        props.put("mail.smtp.auth", "true");

        // 发件人邮箱用户名
        final String username = sendAccount;
        // 发件人邮箱密码
        final String password = sendPwd;
        Session session = Session.getDefaultInstance(props, new Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message msg = new MimeMessage(session);

            // 设置发件人名称
            String fromNick = null;
            try {
                fromNick = MimeUtility.encodeText(sendName);
            } catch (UnsupportedEncodingException e) {
                fromNick = sendAccount;
            }

            // 设置发件人
            msg.setFrom(new InternetAddress(fromNick + "<" + sendAccount + ">"));
            // 设置收件人
            Address to[] = new InternetAddress[tos.length];
            for (int i = 0; i < tos.length; i++) {
                to[i] = new InternetAddress(tos[i]);
            }
            msg.setRecipients(Message.RecipientType.TO, to);
            // 设置邮件主题
            msg.setSubject(subject); // 标题
            // 设置邮件内容
            MimeMultipart multi = new MimeMultipart();
            BodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setContent(content, "text/html;charset=UTF-8");
            multi.addBodyPart(textBodyPart);

            if (null != mailMessage.getAttachs() && mailMessage.getAttachs().size() > 0) {
                for (String attachFile : mailMessage.getAttachs()) {
                    MimeBodyPart bodyPart = new MimeBodyPart();
                    FileDataSource fds = new FileDataSource(attachFile); //得到数据源
                    bodyPart.setDataHandler(new DataHandler(fds)); //得到附件本身并放入BodyPart  
                    try {
                        //得到文件名并编码（防止中文文件名乱码）同样放入BodyPart
                        bodyPart.setFileName(MimeUtility.encodeText(fds.getName()));
                    } catch (UnsupportedEncodingException e) {
                        bodyPart.setFileName(fds.getName());
                    }
                    multi.addBodyPart(bodyPart);
                }
            }

            msg.setContent(multi);
            // 设置发送时间
            msg.setSentDate(new Date());
            msg.saveChanges();
            Transport.send(msg);
        } catch (MessagingException e) {
            try {
                LOG.warn("邮件发送失败,to={},content={}", mapper.writeValueAsString(tos), content, e);
            } catch (JsonProcessingException e1) {
                e1.printStackTrace();
            }
            return false;
        }
        return true;
    }

    public static class MailMessage {

        /**
         * 服务器地址
         */
        private String host;

        /**
         * 服务器端口
         */
        private String port;

        /**
         * 发件人邮箱用户名
         */
        private String sendAccount;

        /**
         * 发件人邮箱密码
         */
        private String sendPwd;

        /**
         * 发件人名称
         */
        private String sendName;

        /**
         * 邮件主题
         */
        private String subject;

        /**
         * 邮件内容
         */
        private String content;

        /**
         * 收件人
         */
        private String[] to;

        /**
         * 附件
         */
        private List<String> attachs;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getSendAccount() {
            return sendAccount;
        }

        public void setSendAccount(String sendAccount) {
            this.sendAccount = sendAccount;
        }

        public String getSendPwd() {
            return sendPwd;
        }

        public void setSendPwd(String sendPwd) {
            this.sendPwd = sendPwd;
        }

        public String getSendName() {
            return sendName;
        }

        public void setSendName(String sendName) {
            this.sendName = sendName;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String[] getTo() {
            return to;
        }

        public void setTo(String... to) {
            this.to = to;
        }

        public List<String> getAttachs() {
            return attachs;
        }

        public void setAttachs(List<String> attachs) {
            this.attachs = attachs;
        }
    }
    
}
