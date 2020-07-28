package com.example.finalproject.sls.data;

public class MessageDTO {
    private Long    id;
    private String  message;
    private boolean isSent;

    public MessageDTO() {    }

    public MessageDTO(String message, boolean isSent) {
        this(null, message, isSent);
    }

    public MessageDTO(Long id, String message, boolean isSent) {
        this.id      = id;
        this.message = message;
        this.isSent  = isSent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }
}
