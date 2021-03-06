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

/**
 * Unblock.
 */
public class Unblock extends NotNullAction {

    @Override
    public String name() {
        return "unblock";
    }

    @Override
    public String help() {
        return "unblock somebody";
    }

    @Override
    public String usage() {
        return "unblock <user_id>";
    }

    @Override
    protected String action(Accounts accounts, Account account, String arguments) throws Mastodon4jRequestException {
        accounts.postUnblock(account.getId()).execute();
        return null;
    }
}
