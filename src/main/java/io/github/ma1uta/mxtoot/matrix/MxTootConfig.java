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

import io.github.ma1uta.matrix.bot.BotConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Matrix bot persistent configuration.
 */
@Entity
@Table(name = "botconfig")
@NamedQueries( {@NamedQuery(name = "matrix.bot.findAll", query = "select d from MxTootConfig d"),
    @NamedQuery(name = "matrix.bot.findByUserId", query = "select d from MxTootConfig d where d.userId = :userId")})
public class MxTootConfig extends BotConfig {

    /**
     * Mastodon server.
     */
    @Column(name = "mstdn_server")
    private String mastodonServer;

    /**
     * Mastodon username.
     */
    @Column(name = "mstdn_client")
    private String mastodonClient;

    /**
     * Mastodon client id.
     */
    @Column(name = "mstdn_client_id")
    private String mastodonClientId;

    /**
     * Mastodon client secret.
     */
    @Column(name = "mstdn_client_secret")
    private String mastodonClientSecret;

    /**
     * Mastodon access token.
     */
    @Column(name = "mstdn_access_token")
    private String mastodonAccessToken;

    /**
     * Mastodon client state.
     */
    @Column(name = "mstdn_timeline")
    @Enumerated(EnumType.STRING)
    private TimelineState timelineState;

    /**
     * Format of the regular posts.
     */
    @Column(name = "mstdn_post_format")
    private String postFormat;

    /**
     * Format of the reply.
     */
    @Column(name = "mstdn_reply_format")
    private String replyFormat;

    /**
     * Format of the boost message.
     */
    @Column(name = "mstdn_boost_format")
    private String boostFormat;

    /**
     * Datetime's format.
     */
    @Column(name = "mstdn_datetime_format")
    private String dateTimeFormat;

    /**
     * Locale of the date time.
     */
    @Column(name = "mstdn_datetime_locale")
    private String dateTimeLocale;

    /**
     * Fetch status by id.
     */
    @Column(name = "fetch_statuses")
    private Boolean fetchMissingStatuses;

    public String getMastodonServer() {
        return mastodonServer;
    }

    public void setMastodonServer(String mastodonServer) {
        this.mastodonServer = mastodonServer;
    }

    public String getMastodonClient() {
        return mastodonClient;
    }

    public void setMastodonClient(String mastodonClient) {
        this.mastodonClient = mastodonClient;
    }

    public String getMastodonClientId() {
        return mastodonClientId;
    }

    public void setMastodonClientId(String mastodonClientId) {
        this.mastodonClientId = mastodonClientId;
    }

    public String getMastodonClientSecret() {
        return mastodonClientSecret;
    }

    public void setMastodonClientSecret(String mastodonClientSecret) {
        this.mastodonClientSecret = mastodonClientSecret;
    }

    public String getMastodonAccessToken() {
        return mastodonAccessToken;
    }

    public void setMastodonAccessToken(String mastodonAccessToken) {
        this.mastodonAccessToken = mastodonAccessToken;
    }

    public TimelineState getTimelineState() {
        return timelineState;
    }

    public void setTimelineState(TimelineState timelineState) {
        this.timelineState = timelineState;
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

    public Boolean getFetchMissingStatuses() {
        return fetchMissingStatuses;
    }

    public void setFetchMissingStatuses(Boolean fetchMissingStatuses) {
        this.fetchMissingStatuses = fetchMissingStatuses;
    }

    public String getDateTimeLocale() {
        return dateTimeLocale;
    }

    public void setDateTimeLocale(String dateTimeLocale) {
        this.dateTimeLocale = dateTimeLocale;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
