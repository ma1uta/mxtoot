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
 * Base class to send messages.
 */
public abstract class AbstractSendMessage extends AbstractStatusCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSendMessage.class);

    @Override
    public String name() {
        return getVisibility().toString().toLowerCase();
    }

    @Override
    public boolean invoke(BotHolder<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>, MxMastodonClient> holder, String roomId,
                          Event event, String arguments) {
        if (!initMastodonClient(holder)) {
            return false;
        }

        EventMethods eventMethods = holder.getMatrixClient().event();
        try {
            new Statuses(holder.getData().getMastodonClient()).postStatus(arguments, null, null, false, null, getVisibility()).execute();
        } catch (Mastodon4jRequestException e) {
            String msg = "Cannot send " + name();
            LOGGER.error(msg, e);
            eventMethods.sendNotice(roomId, msg);
        }
        return true;
    }

    protected abstract Status.Visibility getVisibility();

    @Override
    public String help() {
        return "Send " + name() + " message";
    }

    @Override
    public String usage() {
        return name() + " <message>";
    }
}
