package kr.co.oneclass.notice;

import java.util.Date;

public class NoticeDoamin {
    private int noticeCode;
    private String title;
    private String content;
    private String noticeType;
    private Date noticeDate;

    public NoticeDoamin() {}

    private NoticeDoamin(Builder builder) {
        this.noticeCode = builder.noticeCode;
        this.title = builder.title;
        this.content = builder.content;
        this.noticeType = builder.noticeType;
        this.noticeDate = builder.noticeDate;
    }

    // Getters & Setters
    public int getNoticeCode() { return noticeCode; }
    public void setNoticeCode(int noticeCode) { this.noticeCode = noticeCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getNoticeType() { return noticeType; }
    public void setNoticeType(String noticeType) { this.noticeType = noticeType; }

    public Date getNoticeDate() { return noticeDate; }
    public void setNoticeDate(Date noticeDate) { this.noticeDate = noticeDate; }

    // Builder 패턴
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int noticeCode;
        private String title;
        private String content;
        private String noticeType;
        private Date noticeDate;

        public Builder noticeCode(int noticeCode) {
            this.noticeCode = noticeCode;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder noticeType(String noticeType) {
            this.noticeType = noticeType;
            return this;
        }

        public Builder noticeDate(Date noticeDate) {
            this.noticeDate = noticeDate;
            return this;
        }

        public NoticeDoamin build() {
            return new NoticeDoamin(this);
        }
    }
}