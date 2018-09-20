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
import io.github.ma1uta.matrix.bot.Context;
import io.github.ma1uta.matrix.bot.command.OwnerCommand;
import io.github.ma1uta.mxtoot.mastodon.MxMastodonClient;
import io.github.ma1uta.mxtoot.matrix.MxTootConfig;
import io.github.ma1uta.mxtoot.matrix.MxTootDao;
import io.github.ma1uta.mxtoot.matrix.MxTootPersistentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enable or disable fetch statuses.
 */
public class FetchStatuses extends OwnerCommand<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>, MxMastodonClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FetchStatuses.class);

    @Override
    public String name() {
        return "fetch_statuses";
    }

    @Override
    public boolean ownerInvoke(Context<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>, MxMastodonClient> holder,
                               String roomId, Event event, String arguments) {
        if (arguments == null || arguments.isEmpty()) {
            holder.getMatrixClient().event().sendNotice(roomId, "Usage: " + usage());
            return true;
        }

        holder.getConfig().setFetchMissingStatuses(Boolean.parseBoolean(arguments.trim()));
        return true;
    }

    @Override
    public String help() {
        return "should mastodon client fetch statuses by id (for example replies).";
    }

    @Override
    public String usage() {
        return "fetch_statuses [true|false]";
    }
}
