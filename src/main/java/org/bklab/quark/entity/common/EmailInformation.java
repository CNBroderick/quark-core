package org.bklab.quark.entity.common;

import java.util.StringJoiner;

public class EmailInformation {

    /**
     * SMTP邮件服务器地址
     */
    private String smtpHost;
    /**
     * SMTP邮件服务器发送端口
     */
    private int smtpPort = 25;

    private String senderAddress;
    private String senderPassword;

    /**
     * 发件人名称
     */
    private String senderName;
    /**
     * 邮件标题
     */
    private String emailTitle;
    /**
     * 回信地址
     */
    private String replyAddress;
    private boolean enableSsl = false;

    public String getSmtpHost() {
        return smtpHost;
    }

    public EmailInformation setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
        return this;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public EmailInformation setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
        return this;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public EmailInformation setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
        return this;
    }

    public String getSenderPassword() {
        return senderPassword;
    }

    public EmailInformation setSenderPassword(String senderPassword) {
        this.senderPassword = senderPassword;
        return this;
    }

    public String getSenderName() {
        return senderName;
    }

    public EmailInformation setSenderName(String senderName) {
        this.senderName = senderName;
        return this;
    }

    public String getEmailTitle() {
        return emailTitle;
    }

    public EmailInformation setEmailTitle(String emailTitle) {
        this.emailTitle = emailTitle;
        return this;
    }

    public String getReplyAddress() {
        return replyAddress;
    }

    public EmailInformation setReplyAddress(String replyAddress) {
        this.replyAddress = replyAddress;
        return this;
    }

    public boolean isEnableSsl() {
        return enableSsl;
    }

    public EmailInformation setEnableSsl(boolean enableSsl) {
        this.enableSsl = enableSsl;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EmailInformation.class.getSimpleName() + "{", "\n}")
                .add("\n\tsmtpHost: '" + smtpHost + "'")
                .add("\n\tsmtpPort: " + smtpPort)
                .add("\n\tsenderAddress: '" + senderAddress + "'")
                .add("\n\tsenderPassword: '" + senderPassword + "'")
                .add("\n\tsenderName: '" + senderName + "'")
                .add("\n\temailTitle: '" + emailTitle + "'")
                .add("\n\treplyAddress: '" + replyAddress + "'")
                .add("\n\tenableSsl: " + enableSsl)
                .toString();
    }
}
