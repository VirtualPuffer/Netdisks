package com.virtualpuffer.netdisk.controller.base;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

public abstract class BaseLogger {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    protected MessageSource message;

    public BaseLogger() {
    }

    protected String getMessage(String key, Object... param) {
        return this.message.getMessage(key, param, Locale.getDefault());
    }

    protected void info(String messageKey, Object... param) {
        this.logger.info(this.getMessage(messageKey, param));
    }

    protected void error(String messageKey, Object... param) {
        this.logger.error(this.getMessage(messageKey, param));
    }

    protected void warn(String messageKey, Object... param) {
        this.logger.warn(this.getMessage(messageKey, param));
    }

    protected void throwException(String messageKey, Object... param) throws RuntimeException {
        throw new RuntimeException(this.getMessage(messageKey, param));
    }

    protected boolean isNull(Object value) {
        if (value == null) {
            return true;
        } else {
            return value instanceof String && StringUtils.isBlank((String)value);
        }
    }
}
