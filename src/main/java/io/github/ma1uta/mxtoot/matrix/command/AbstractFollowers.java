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

import com.sys1yagi.mastodon4j.api.Pageable;
import com.sys1yagi.mastodon4j.api.entity.Account;
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import com.sys1yagi.mastodon4j.api.method.Accounts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

/**
 * Show followers.
 */
public abstract class AbstractFollowers extends NotNullAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFollowers.class);

    protected abstract Pageable<Account> getAccounts(Accounts accounts, Account account) throws Mastodon4jRequestException;

    protected abstract Pageable<Account> getAccounts(Accounts accounts, Account account, long maxId) throws Mastodon4jRequestException;

    @Override
    protected String action(Accounts accounts, Account account, String arguments) throws Mastodon4jRequestException {
        String[] params = arguments.split("\\s+");
        long pageNumber = 1L;

        if (params.length > 1) {
            try {
                pageNumber = Long.parseLong(params[1]);
            } catch (NumberFormatException e) {
                LOGGER.error("Wrong parameter", e);
                throw new Mastodon4jRequestException(e);
            }
        }

        Pageable<Account> pageable = getAccounts(accounts, account);
        long maxId;
        while (pageNumber > 1) {
            if (pageable.getLink() == null) {
                return "Cannot found followers.";
            }
            maxId = pageable.getLink().getSinceId();
            pageable = getAccounts(accounts, account, maxId);
            pageNumber--;
        }
        return pageable.getPart().stream().map(Account::getAcct).collect(Collectors.joining("\n"));
    }
}
