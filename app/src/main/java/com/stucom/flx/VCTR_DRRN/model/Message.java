package com.stucom.flx.VCTR_DRRN.model;

public class Message {
    private String Id;
    private String from_id;
    private String to_id;
    private String sent_at;
    private String text;

    public Message(){}

    public String getId() { return Id; }

    public void setId(String id) { Id = id; }

    public String getFrom_id() { return from_id; }

    public void setFrom_id(String from_id) { this.from_id = from_id; }

    public String getTo_id() { return to_id; }

    public void setTo_id(String to_id) { this.to_id = to_id; }

    public String getSent_at() { return sent_at; }

    public void setSent_at(String sent_at) { this.sent_at = sent_at; }

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }
}
