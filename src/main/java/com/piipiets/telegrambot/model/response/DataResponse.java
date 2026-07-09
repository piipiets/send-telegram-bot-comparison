package com.piipiets.telegrambot.model.response;

import java.util.Date;

public record DataResponse<T>(
        String result,
        String detail,
        Date date,
        int code,
        T data
) {
}