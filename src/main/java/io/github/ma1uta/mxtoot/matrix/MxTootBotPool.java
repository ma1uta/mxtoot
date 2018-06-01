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

package io.github.ma1uta.mxtoot.matrix;

import io.dropwizard.lifecycle.Managed;
import io.github.ma1uta.matrix.Id;
import io.github.ma1uta.matrix.bot.AbstractBotPool;
import io.github.ma1uta.matrix.bot.Bot;
import io.github.ma1uta.matrix.bot.Command;
import io.github.ma1uta.mxtoot.BotConfiguration;
import io.github.ma1uta.mxtoot.mastodon.MxMastodonClient;
import io.github.ma1uta.mxtoot.matrix.command.AbstractStatusCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import javax.ws.rs.client.Client;

/**
 * Bot service.
 */
public class MxTootBotPool extends AbstractBotPool<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>, MxMastodonClient> implements
    Managed {

    private static final Logger LOGGER = LoggerFactory.getLogger(MxTootBotPool.class);

    private final BotConfiguration botConfiguration;

    public MxTootBotPool(BotConfiguration botConfiguration, MxTootPersistentService<MxTootDao> service, Client client,
                         List<Class<? extends Command<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>,
                             MxMastodonClient>>> cmds) {
        super(botConfiguration.getHomeserverUrl(), botConfiguration.getDisplayName(), client,
            botConfiguration.getAsToken(), service, cmds, botConfiguration.getRunState());
        this.botConfiguration = botConfiguration;
    }

    public BotConfiguration getBotConfiguration() {
        return botConfiguration;
    }

    @Override
    protected MxTootConfig createConfig(String username) {
        MxTootConfig config = new MxTootConfig();
        config.setUserId(username);
        config.setDisplayName(getDisplayName());
        config.setDeviceId(UUID.randomUUID().toString());

        String localpart = Id.localpart(username);
        int nameIndex = localpart.indexOf("_");
        if (nameIndex > -1 && nameIndex < localpart.length()) {
            config.setMastodonClient(localpart.substring(nameIndex + 1));
        } else {
            config.setMastodonClient(UUID.randomUUID().toString());
        }
        config.setPostFormat(getBotConfiguration().getPostFormat());
        config.setReplyFormat(getBotConfiguration().getReplyFormat());
        config.setBoostFormat(getBotConfiguration().getBoostFormat());
        config.setDateTimeFormat(getBotConfiguration().getDateTimeFormat());
        config.setDateTimeLocale(getBotConfiguration().getDateTimeLocale());
        config.setFetchMissingStatuses(getBotConfiguration().getFetchMissingStatuses());
        config.setPrefix(getBotConfiguration().getPrefix());

        return config;
    }

    @Override
    protected void initializeBot(Bot<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>, MxMastodonClient> bot) {
        bot.setInitAction((holder, dao) -> {
            if (TimelineState.AUTO.equals(holder.getConfig().getTimelineState())) {
                AbstractStatusCommand.initMastodonClient(holder);
                if (!holder.getData().streaming()) {
                    LOGGER.error("Cannot streaming: " + holder.getConfig().getId());
                    holder.getMatrixClient().event().sendNotice(holder.getConfig().getRoomId(), "Cannot streaming.");
                }
            }
        });
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() throws InterruptedException {
        super.stop();
    }
}
