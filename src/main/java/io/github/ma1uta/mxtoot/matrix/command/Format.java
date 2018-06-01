/*
 * Copyright sablintolya@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.ma1uta.mxtoot.matrix.command;

import io.github.ma1uta.matrix.Event;
import io.github.ma1uta.matrix.bot.BotHolder;
import io.github.ma1uta.matrix.bot.Command;
import io.github.ma1uta.matrix.client.EventMethods;
import io.github.ma1uta.matrix.client.MatrixClient;
import io.github.ma1uta.mxtoot.mastodon.MxMastodonClient;
import io.github.ma1uta.mxtoot.matrix.MxTootConfig;
import io.github.ma1uta.mxtoot.matrix.MxTootDao;
import io.github.ma1uta.mxtoot.matrix.MxTootPersistentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Show or fetch templates of the posts, boosts, replies, datetime or datetime locale.
 */
public class Format implements Command<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>, MxMastodonClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Format.class);

    @Override
    public String name() {
        return "format";
    }

    @Override
    public void invoke(BotHolder<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>, MxMastodonClient> holder, Event event,
                       String arguments) {
        MxTootConfig config = holder.getConfig();
        if (config.getOwner() != null && !config.getOwner().equals(event.getSender())) {
            return;
        }

        MatrixClient matrixClient = holder.getMatrixClient();

        if (arguments == null || arguments.isEmpty()) {
            matrixClient.event().sendNotice(config.getRoomId(), "Usage: " + usage());
            return;
        }
        String trimmed = arguments.trim();
        int spaceIndex = trimmed.indexOf(" ");
        if (spaceIndex == -1) {
            showTemplate(trimmed, config, matrixClient);
        } else {
            setTemplate(trimmed.substring(0, spaceIndex), trimmed.substring(spaceIndex + 1), config, matrixClient, holder.getData());
        }
    }

    @Override
    public String help() {
        return "show or set a template of the post, reply, boost, date or locale of the date (can invoke only owner).";
    }

    @Override
    public String usage() {
        return "format [post|reply|boost|datetime|locale] [<template or datetime format or locale>]";
    }

    protected void showTemplate(String templateName, MxTootConfig config, MatrixClient matrixClient) {
        EventMethods event = matrixClient.event();
        switch (templateName) {
            case "post":
                event.sendNotice(config.getRoomId(), config.getPostFormat());
                break;
            case "reply":
                event.sendNotice(config.getRoomId(), config.getReplyFormat());
                break;
            case "boost":
                event.sendNotice(config.getRoomId(), config.getBoostFormat());
                break;
            case "datetime":
                event.sendNotice(config.getRoomId(), config.getDateTimeFormat());
                break;
            case "locale":
                event.sendNotice(config.getRoomId(), config.getDateTimeLocale());
                break;
            default:
                event.sendNotice(config.getRoomId(), "Unknown template name: " + templateName);
        }
    }

    protected void setTemplate(String templateName, String template, MxTootConfig config, MatrixClient matrixClient,
                               MxMastodonClient mastodonClient) {
        switch (templateName) {
            case "post":
                config.setPostFormat(template);
                mastodonClient.setPostTemplate(null);
                break;
            case "reply":
                config.setReplyFormat(template);
                mastodonClient.setReplyTemplate(null);
                break;
            case "boost":
                config.setBoostFormat(template);
                mastodonClient.setBoostTemplate(null);
                break;
            case "datetime":
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(template, new Locale(config.getDateTimeLocale()));
                    String preview = LocalDateTime.now().format(formatter);
                    matrixClient.event().sendNotice(config.getRoomId(), "Current datetime with specified format: " + preview);
                    config.setDateTimeFormat(template);
                } catch (IllegalArgumentException | DateTimeException e) {
                    LOGGER.warn("Wrong datetime format: " + template, e);
                }
                break;
            case "locale":
                new Locale(template);
                config.setDateTimeLocale(template);
                break;
            default:
                matrixClient.event().sendNotice(config.getRoomId(), "Unknown template name: " + templateName);
        }
    }
}
