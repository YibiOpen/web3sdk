package org.bcos.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * create at 2018/6/12 4:15 PM
 *
 * @author <a href="mailto:yaolijun@hz-health.cn">Paul Yao</a>
 * @version %I%, %G%
 * @see
 */
public class NotificationService {

    private Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private String mailHost;
    private String mailPort;
    private String mailSenderAddress;
    private String mailSenderPwd;
    private String mailSenderName;
    private List<String> receiverAddressList;

    public void sendMail(Exception e) {
        sendMail(writeStackTraceToString(e));
    }

    public void sendMail(String content) {
        EmailUtils.MailMessage mail = new EmailUtils.MailMessage();
        // 发送邮件
        mail.setHost(mailHost);
        mail.setPort(mailPort);
        mail.setSendAccount(mailSenderAddress);
        mail.setSendPwd(mailSenderPwd);
        mail.setSendName(mailSenderName);
        mail.setSubject("bcos服务检测异常！");
        mail.setContent(content);
        mail.setTo(receiverAddressList.toArray(new String[receiverAddressList.size()]));
        if (!EmailUtils.sslSend(mail)) {
            logger.error("bcos服务检测异常，邮件发送失败！");
            return;
        }
    }

    public String getMailHost() {
        return mailHost;
    }

    public void setMailHost(String mailHost) {
        this.mailHost = mailHost;
    }

    public String getMailPort() {
        return mailPort;
    }

    public void setMailPort(String mailPort) {
        this.mailPort = mailPort;
    }

    public String getMailSenderAddress() {
        return mailSenderAddress;
    }

    public void setMailSenderAddress(String mailSenderAddress) {
        this.mailSenderAddress = mailSenderAddress;
    }

    public String getMailSenderPwd() {
        return mailSenderPwd;
    }

    public void setMailSenderPwd(String mailSenderPwd) {
        this.mailSenderPwd = mailSenderPwd;
    }

    public String getMailSenderName() {
        return mailSenderName;
    }

    public void setMailSenderName(String mailSenderName) {
        this.mailSenderName = mailSenderName;
    }

    public List<String> getReceiverAddressList() {
        return receiverAddressList;
    }

    public void setReceiverAddressList(List<String> receiverAddressList) {
        this.receiverAddressList = receiverAddressList;
    }

    public static String writeStackTraceToString(Throwable t) {
        if (t == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw, true));
        return sw.getBuffer().toString();
    }

}
