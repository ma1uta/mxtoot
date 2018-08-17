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

import com.sys1yagi.mastodon4j.api.entity.Status;
import com.sys1yagi.mastodon4j.api.method.Statuses;
import io.github.ma1uta.matrix.Event;
import io.github.ma1uta.matrix.bot.BotHolder;
import io.github.ma1uta.matrix.client.methods.EventMethods;
import io.github.ma1uta.mxtoot.mastodon.MxMastodonClient;
import io.github.ma1uta.mxtoot.matrix.MxTootConfig;
import io.github.ma1uta.mxtoot.matrix.MxTootDao;
import io.github.ma1uta.mxtoot.matrix.MxTootPersistentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reply.
 */
public class Reply implements StatusCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(Reply.class);

    @Override
    public String name() {
        return "reply";
    }

    @Override
    public boolean invoke(BotHolder<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>, MxMastodonClient> holder, String roomId,
                          Event event, String arguments) {
        EventMethods eventMethods = holder.getMatrixClient().event();

        if (!StatusCommand.initMastodonClient(holder)) {
            return false;
        }

        if (arguments == null || arguments.trim().isEmpty()) {
            eventMethods.sendNotice(roomId, "Usage: " + usage());
            return true;
        }

        String trimmed = arguments.trim();
        int spaceIndex = trimmed.indexOf(" ");
        if (spaceIndex == -1) {
            eventMethods.sendNotice(roomId, "Usage: " + usage());
            return true;
        }

        Long statusId;
        try {
            statusId = Long.parseLong(trimmed.substring(0, spaceIndex));
        } catch (NumberFormatException e) {
            LOGGER.error("Wrong status id", e);
            eventMethods.sendNotice(roomId, "Status id is not a number.\nUsage: " + usage());
            return true;
        }
        String message = trimmed.substring(spaceIndex);

        try {
            Statuses statuses = new Statuses(holder.getData().getMastodonClient());
            Status origin = statuses.getStatus(statusId).execute();
            Status.Visibility visibility = visibilityByString(origin.getVisibility());
            statuses.postStatus(message, statusId, null, false, null, visibility).execute();
        } catch (Exception e) {
            LOGGER.error("Cannot reply", e);
            eventMethods.sendNotice(roomId, "Cannot reply: " + e.getMessage());
        }
        return true;
    }

    protected Status.Visibility visibilityByString(String visibility) {
        for (Status.Visibility item : Status.Visibility.values()) {
            if (item.getValue().equals(visibility)) {
                return item;
            }
        }
        return null;
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
