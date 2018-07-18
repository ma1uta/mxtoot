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

import com.google.gson.Gson;
import com.sys1yagi.mastodon4j.MastodonClient;
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken;
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import com.sys1yagi.mastodon4j.api.method.Apps;
import io.github.ma1uta.matrix.Event;
import io.github.ma1uta.matrix.bot.BotHolder;
import io.github.ma1uta.matrix.bot.command.OwnerCommand;
import io.github.ma1uta.matrix.client.methods.EventMethods;
import io.github.ma1uta.mxtoot.mastodon.MxMastodonClient;
import io.github.ma1uta.mxtoot.matrix.MxTootConfig;
import io.github.ma1uta.mxtoot.matrix.MxTootDao;
import io.github.ma1uta.mxtoot.matrix.MxTootPersistentService;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Finish registration of the mastodon client.
 */
public class AuthorizeMastodonClient extends OwnerCommand<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>, MxMastodonClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizeMastodonClient.class);

    @Override
    public String name() {
        return "auth";
    }

    @Override
    public boolean ownerInvoke(BotHolder<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>, MxMastodonClient> holder,
                               String roomId, Event event, String arguments) {
        MxTootConfig config = holder.getConfig();
        EventMethods eventMethods = holder.getMatrixClient().event();

        if (config.getMastodonClientId() == null || config.getMastodonClientId().trim().isEmpty()
            || config.getMastodonClientSecret() == null || config.getMastodonClientSecret().trim().isEmpty()) {
            eventMethods.sendNotice(roomId, "Start registration by invoking !reg command");
            return true;
        }

        if (arguments == null || arguments.trim().isEmpty()) {
            eventMethods.sendNotice(roomId, "Usage: " + usage());
            return true;
        }

        MastodonClient client = new MastodonClient.Builder(config.getMastodonServer(), new OkHttpClient.Builder(), new Gson()).build();

        Apps apps = new Apps(client);
        try {
            AccessToken accessToken = apps.getAccessToken(config.getMastodonClientId(), config.getMastodonClientSecret(), arguments.trim())
                .execute();
            config.setMastodonAccessToken(accessToken.getAccessToken());
            config.setMastodonClientSecret(null);
            config.setMastodonClientId(null);
        } catch (Mastodon4jRequestException e) {
            String msg = "Cannot get access token: ";
            LOGGER.error(msg, e);
            eventMethods.sendNotice(roomId, msg + e.getMessage());
        }
        return true;
    }

    @Override
    public String help() {
        return "authorize mastodon client (can invoke only owner).";
    }

    @Override
    public String usage() {
        return "auth <auth code>";
    }
}
