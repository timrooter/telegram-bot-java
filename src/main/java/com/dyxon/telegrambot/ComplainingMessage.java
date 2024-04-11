package com.dyxon.telegrambot;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class ComplainingMessage {
    ZonedDateTime zonedDateTime;
    String message;
    String position;
    String happenedTime;
    public void setZonedDateTime(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }
}

