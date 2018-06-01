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

package io.github.ma1uta.mxtoot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.db.DataSourceFactory;
import io.github.ma1uta.matrix.bot.Command;
import io.github.ma1uta.matrix.bot.RunState;
import io.github.ma1uta.mxtoot.mastodon.MxMastodonClient;
import io.github.ma1uta.mxtoot.matrix.MxTootConfig;
import io.github.ma1uta.mxtoot.matrix.MxTootDao;
import io.github.ma1uta.mxtoot.matrix.MxTootPersistentService;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * BotApplication configuration.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BotConfiguration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty("jerseyClient")
    private JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();

    @NotEmpty
    @URL
    private String homeserverUrl;

    private String displayName;

    @NotEmpty
    @JsonProperty("hs_token")
    private String hsToken;

    @NotEmpty
    @JsonProperty("as_token")
    private String asToken;

    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();

    @NotNull
    private RunState runState;

    @NotEmpty
    @NotNull
    private String postFormat;

    @NotEmpty
    @NotNull
    private String replyFormat;

    @NotEmpty
    @NotNull
    private String boostFormat;

    @NotEmpty
    @NotNull
    private String dateTimeFormat;

    @NotEmpty
    @NotNull
    private String dateTimeLocale;

    private Boolean fetchMissingStatuses;

    @NotNull
    private boolean strictMode = true;

    private List<Class<? extends Command<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>, MxMastodonClient>>> commands =
        new ArrayList<>();

    private String prefix = "!";

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    public JerseyClientConfiguration getJerseyClient() {
        return jerseyClient;
    }

    public void setJerseyClient(JerseyClientConfiguration jerseyClient) {
        this.jerseyClient = jerseyClient;
    }

    public String getHomeserverUrl() {
        return homeserverUrl;
    }

    public void setHomeserverUrl(String homeserverUrl) {
        this.homeserverUrl = homeserverUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getHsToken() {
        return hsToken;
    }

    public void setHsToken(String hsToken) {
        this.hsToken = hsToken;
    }

    public String getAsToken() {
        return asToken;
    }

    public void setAsToken(String asToken) {
        this.asToken = asToken;
    }

    public List<Class<? extends Command<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>, MxMastodonClient>>> getCommands() {
        return commands;
    }

    public void setCommands(
        List<Class<? extends Command<MxTootConfig, MxTootDao, MxTootPersistentService<MxTootDao>, MxMastodonClient>>> commands) {
        this.commands = commands;
    }

    public RunState getRunState() {
        return runState;
    }

    public void setRunState(RunState runState) {
        this.runState = runState;
    }

    public boolean isStrictMode() {
        return strictMode;
    }

    public void setStrictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }

    public DataSourceFactory getDatabase() {
        return database;
    }

    public void setDatabase(DataSourceFactory database) {
        this.database = database;
    }

    public String getPostFormat() {
        return postFormat;
    }

    public void setPostFormat(String postFormat) {
        this.postFormat = postFormat;
    }

    public String getReplyFormat() {
        return replyFormat;
    }

    public void setReplyFormat(String replyFormat) {
        this.replyFormat = replyFormat;
    }

    public String getBoostFormat() {
        return boostFormat;
    }

    public void setBoostFormat(String boostFormat) {
        this.boostFormat = boostFormat;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    public String getDateTimeLocale() {
        return dateTimeLocale;
    }

    public void setDateTimeLocale(String dateTimeLocale) {
        this.dateTimeLocale = dateTimeLocale;
    }

    public Boolean getFetchMissingStatuses() {
        return fetchMissingStatuses;
    }

    public void setFetchMissingStatuses(Boolean fetchMissingStatuses) {
        this.fetchMissingStatuses = fetchMissingStatuses;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
