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
import io.github.ma1uta.matrix.client.EventMethods;
import io.github.ma1uta.mxtoot.mastodon.MxMastodonClient;
import io.github.ma1uta.mxtoot.matrix.MxTootConfig;
import io.github.ma1uta.mxtoot.matrix.MxTootDao;
import io.github.ma1uta.mxtoot.matrix.MxTootPersistentService;
import io.github.ma1uta.mxtoot.matrix.TimelineState;

/**
 * Run mastodon timeline.
 */
public class Timeline extends AbstractStatusCommand {
    @Override
    public String name() {
        return "timeline";
    }

    @Override
    public void invoke(BotHolder<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>, MxMastodonClient> holder, Event event,
                       String arguments) {
        MxTootConfig config = holder.getConfig();
        if (config.getOwner() != null && !config.getOwner().equals(event.getSender())) {
            return;
        }

        EventMethods eventMethods = holder.getMatrixClient().event();
        if (arguments == null || arguments.trim().isEmpty()) {
            eventMethods.sendNotice(config.getRoomId(), "Usage: " + help());
        } else {
            TimelineState clientState = TimelineState.valueOf(arguments.trim().toUpperCase());
            config.setTimelineState(clientState);

            initMastodonClient(holder);

            switch (clientState) {
                case ON:
                case AUTO:
                    if (!holder.getData().streaming()) {
                        eventMethods.sendNotice(config.getRoomId(), "Cannot streaming");
                    }
                    break;
                case OFF:
                    holder.getData().get();
                    break;
                default:
                    eventMethods.sendNotice(config.getRoomId(), "Unknown status " + clientState);
            }
        }
    }

    @Override
    public String help() {
        return "start, stop or autostart timeline (only owner can invoke).";
    }

    @Override
    public String usage() {
        return "timeline [on|off|auto]";
    }
}
