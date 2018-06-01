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

import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import com.sys1yagi.mastodon4j.api.method.Statuses;
import io.github.ma1uta.matrix.Event;
import io.github.ma1uta.matrix.bot.BotHolder;
import io.github.ma1uta.matrix.client.EventMethods;
import io.github.ma1uta.mxtoot.mastodon.MxMastodonClient;
import io.github.ma1uta.mxtoot.matrix.MxTootConfig;
import io.github.ma1uta.mxtoot.matrix.MxTootDao;
import io.github.ma1uta.mxtoot.matrix.MxTootPersistentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reply.
 */
public class Reply extends AbstractStatusCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(Reply.class);

    @Override
    public String name() {
        return "reply";
    }

    @Override
    public void invoke(BotHolder<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>, MxMastodonClient> holder, Event event,
                       String arguments) {
        MxTootConfig config = holder.getConfig();
        EventMethods eventMethods = holder.getMatrixClient().event();

        if (!initMastodonClient(holder)) {
            return;
        }

        if (arguments == null || arguments.trim().isEmpty()) {
            eventMethods.sendNotice(config.getRoomId(), "Usage: " + usage());
            return;
        }

        String trimmed = arguments.trim();
        int spaceIndex = trimmed.indexOf(" ");
        if (spaceIndex == -1) {
            eventMethods.sendNotice(config.getRoomId(), "Usage: " + usage());
            return;
        }

        Long statusId;
        try {
            statusId = Long.parseLong(trimmed.substring(0, spaceIndex));
        } catch (NumberFormatException e) {
            LOGGER.error("Wrong status id", e);
            eventMethods.sendNotice(config.getRoomId(), "Status id is not a number.\nUsage: " + usage());
            return;
        }
        String message = trimmed.substring(spaceIndex);

        try {
            new Statuses(holder.getData().getMastodonClient()).postStatus(message, statusId, null, false, null).execute();
        } catch (Mastodon4jRequestException e) {
            LOGGER.error("Cannot toot", e);
            eventMethods.sendNotice(config.getRoomId(), "Cannot toot: " + e.getMessage());
        }
    }

    @Override
    public String help() {
        return "reply";
    }

    @Override
    public String usage() {
        return "reply <status_id> <message>";
    }
}
