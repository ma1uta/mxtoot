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
import io.github.ma1uta.matrix.bot.BotHolder;
import io.github.ma1uta.matrix.bot.Command;
import io.github.ma1uta.mxtoot.mastodon.MxMastodonClient;
import io.github.ma1uta.mxtoot.matrix.MxTootConfig;
import io.github.ma1uta.mxtoot.matrix.MxTootDao;
import io.github.ma1uta.mxtoot.matrix.MxTootPersistentService;
import okhttp3.OkHttpClient;

/**
 * Common parent of the status commands with Mastodon client initialization.
 */
public abstract class AbstractStatusCommand implements
    Command<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>, MxMastodonClient> {

    /**
     * Initialize mastodon client.
     *
     * @param holder dot's holder.
     * @return {@code true} if the initialization was succeed else {@code false}.
     */
    public static boolean initMastodonClient(
        BotHolder<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>, MxMastodonClient> holder) {
        if (holder.getData() == null) {
            if (holder.getConfig().getMastodonAccessToken() == null || holder.getConfig().getMastodonAccessToken().trim().isEmpty()) {
                holder.getMatrixClient().event()
                    .sendNotice(holder.getConfig().getRoomId(), "Client isn't initialized, start registration via !reg command.");
                return false;
            } else {
                MxTootConfig config = holder.getConfig();
                MastodonClient client = new MastodonClient.Builder(config.getMastodonServer(), new OkHttpClient.Builder(), new Gson())
                    .useStreamingApi().accessToken(config.getMastodonAccessToken()).build();

                MxMastodonClient mastodonClient = new MxMastodonClient(client, holder);
                holder.setData(mastodonClient);
                holder.addShutdownListener(mastodonClient);
                return true;
            }
        }

        return true;
    }
}
