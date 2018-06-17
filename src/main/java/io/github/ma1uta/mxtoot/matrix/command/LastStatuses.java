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

import com.sys1yagi.mastodon4j.api.Range;
import com.sys1yagi.mastodon4j.api.entity.Status;
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import com.sys1yagi.mastodon4j.api.method.Timelines;
import io.github.ma1uta.matrix.Event;
import io.github.ma1uta.matrix.bot.BotHolder;
import io.github.ma1uta.matrix.client.EventMethods;
import io.github.ma1uta.mxtoot.mastodon.MxMastodonClient;
import io.github.ma1uta.mxtoot.matrix.MxTootConfig;
import io.github.ma1uta.mxtoot.matrix.MxTootDao;
import io.github.ma1uta.mxtoot.matrix.MxTootPersistentService;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Boost.
 */
public class LastStatuses extends AbstractStatusCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(LastStatuses.class);

    private static final long DEFAULT_COUNT = 20L;

    @Override
    public String name() {
        return "last";
    }

    @Override
    public boolean invoke(BotHolder<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>, MxMastodonClient> holder, String roomId,
                          Event event, String arguments) {
        EventMethods eventMethods = holder.getMatrixClient().event();

        if (!initMastodonClient(holder)) {
            return false;
        }

        long last = DEFAULT_COUNT;
        if (arguments != null && !arguments.trim().isEmpty()) {
            try {
                last = Long.parseLong(arguments.trim());
            } catch (NumberFormatException e) {
                LOGGER.error("Failed parse arguments: " + arguments.trim());
                eventMethods.sendNotice(roomId, "Usage: " + usage());
                return true;
            }
        }

        try {
            Timelines timelines = new Timelines(holder.getData().getMastodonClient());
            long lastStatusId = Long.MAX_VALUE;
            Queue<Status> statusQueue = new ArrayDeque<>();
            for (int i = 0; i < last; i++) {
                if (statusQueue.isEmpty()) {
                    statusQueue.addAll(timelines.getHome(new Range(lastStatusId)).execute().getPart());
                }
                Status status = statusQueue.poll();
                if (status != null) {
                    lastStatusId = status.getId();
                    String message = holder.getData().writeStatus(status);
                    eventMethods.sendFormattedNotice(roomId, Jsoup.parse(message).text(), message);
                }
            }
        } catch (Mastodon4jRequestException e) {
            LOGGER.error("Cannot fetch last statuses", e);
            eventMethods.sendNotice(roomId, "Cannot fetch last statuses: " + e.getMessage());
        }
        return true;
    }

    @Override
    public String help() {
        return "write last <count> statuses (20 by default).";
    }

    @Override
    public String usage() {
        return "last [<count>]";
    }
}
