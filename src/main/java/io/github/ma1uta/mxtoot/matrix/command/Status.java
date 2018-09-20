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
import io.github.ma1uta.matrix.bot.Context;
import io.github.ma1uta.matrix.client.methods.EventMethods;
import io.github.ma1uta.mxtoot.mastodon.MxMastodonClient;
import io.github.ma1uta.mxtoot.matrix.MxTootConfig;
import io.github.ma1uta.mxtoot.matrix.MxTootDao;
import io.github.ma1uta.mxtoot.matrix.MxTootPersistentService;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Boost.
 */
public class Status implements StatusCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(Status.class);

    @Override
    public String name() {
        return "status";
    }

    @Override
    public boolean invoke(Context<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>, MxMastodonClient> context, String roomId,
                          Event event, String arguments) {
        EventMethods eventMethods = context.getMatrixClient().event();

        if (!StatusCommand.initMastodonClient(context)) {
            return false;
        }

        if (arguments == null || arguments.trim().isEmpty()) {
            eventMethods.sendNotice(roomId, "Usage: " + usage());
            return true;
        }
        long statusId;
        try {
            statusId = Long.parseLong(arguments.trim());
        } catch (NumberFormatException e) {
            LOGGER.error("Failed parse arguments: " + arguments.trim());
            eventMethods.sendNotice(roomId, "Status id is not a number");
            return true;
        }

        try {
            MxMastodonClient mastodonClient = context.getData();
            com.sys1yagi.mastodon4j.api.entity.Status status = new Statuses(mastodonClient.getMastodonClient()).getStatus(statusId)
                .execute();
            String message = mastodonClient.writeStatus(status);
            eventMethods.sendFormattedNotice(roomId, Jsoup.parse(message).text(), message);
        } catch (Mastodon4jRequestException e) {
            LOGGER.error("Cannot fetch status", e);
            eventMethods.sendNotice(roomId, "Cannot fetch status: " + e.getMessage());
        }
        return true;
    }

    @Override
    public String help() {
        return "write status by its id.";
    }

    @Override
    public String usage() {
        return "status <status_id>";
    }
}
