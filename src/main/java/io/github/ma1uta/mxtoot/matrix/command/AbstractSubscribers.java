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

import com.sys1yagi.mastodon4j.api.entity.Account;
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import com.sys1yagi.mastodon4j.api.method.Accounts;
import io.github.ma1uta.matrix.Event;
import io.github.ma1uta.matrix.bot.BotHolder;
import io.github.ma1uta.matrix.bot.command.OwnerCommand;
import io.github.ma1uta.matrix.client.methods.EventMethods;
import io.github.ma1uta.mxtoot.mastodon.MxMastodonClient;
import io.github.ma1uta.mxtoot.matrix.MxTootConfig;
import io.github.ma1uta.mxtoot.matrix.MxTootDao;
import io.github.ma1uta.mxtoot.matrix.MxTootPersistentService;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Stop follow to somebody.
 */
public abstract class AbstractSubscribers extends
    OwnerCommand<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>, MxMastodonClient> implements
    StatusCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSubscribers.class);

    protected abstract String action(Accounts accounts, Account account, String arguments) throws Mastodon4jRequestException;

    protected abstract Pair<Boolean, String> checkArgument(
        BotHolder<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>, MxMastodonClient> holder, String arguments);

    @Override
    protected boolean ownerInvoke(BotHolder<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>, MxMastodonClient> holder,
                                  String roomId, Event event, String arguments) {
        if (!StatusCommand.initMastodonClient(holder)) {
            return false;
        }

        MxMastodonClient mastodonClient = holder.getData();
        List<Account> accounts;
        Accounts accountsMethods = new Accounts(mastodonClient.getMastodonClient());
        EventMethods eventMethods = holder.getMatrixClient().event();

        Pair<Boolean, String> validation = checkArgument(holder, arguments);
        if (!validation.getLeft()) {
            eventMethods.sendNotice(roomId, validation.getRight());
            return false;
        }

        String searchTerm = validation.getRight();
        try {
            accounts = accountsMethods.getAccountSearch(searchTerm).execute();
        } catch (Mastodon4jRequestException e) {
            LOGGER.error("Failed to search account", e);
            eventMethods.sendNotice(roomId, String.format("Failed to search %s", arguments));
            return false;
        }
        List<Account> candidates = accounts.stream().filter(a -> searchTerm.equalsIgnoreCase(a.getAcct())).collect(Collectors.toList());

        if (candidates.size() == 1) {
            try {
                Account account = accounts.get(0);
                String result = action(accountsMethods, account, arguments);
                eventMethods.sendNotice(roomId, result != null ? result : String.format("%s to: %s", name(), account.getUrl()));
            } catch (Mastodon4jRequestException e) {
                LOGGER.error(String.format("Failed to %s", name()), e);
                eventMethods.sendNotice(roomId, String.format("Failed to %s: %s", name(), e.getMessage()));
                return false;
            }
        } else if (accounts.isEmpty()) {
            eventMethods.sendNotice(roomId, "Not found.");
            return true;
        } else {
            String candidateNames = accounts.stream().map(Account::getAcct).collect(Collectors.joining(", "));
            eventMethods.sendNotice(roomId, String.format("Possible candidates: %s", candidateNames));
            return true;
        }

        return false;
    }
}
