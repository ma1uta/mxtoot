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

package io.github.ma1uta.mxtoot.mastodon;

import com.sys1yagi.mastodon4j.MastodonClient;
import com.sys1yagi.mastodon4j.api.Dispatcher;
import com.sys1yagi.mastodon4j.api.Handler;
import com.sys1yagi.mastodon4j.api.Shutdownable;
import com.sys1yagi.mastodon4j.api.entity.Notification;
import com.sys1yagi.mastodon4j.api.entity.Status;
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.util.function.Consumer;

/**
 * Rewrited version of the {@link com.sys1yagi.mastodon4j.api.method.Streaming} to add the feature to retry connection.
 */
public class Streaming {

    private static final Logger LOGGER = LoggerFactory.getLogger(Streaming.class);

    private final MastodonClient client;
    private final boolean retryable;
    private final Consumer<Response> errorHandler;

    public Streaming(MastodonClient client, boolean retryable, Consumer<Response> errorHandler) {
        this.client = client;
        this.retryable = retryable;
        this.errorHandler = errorHandler;
    }

    /**
     * Fetch user timeline.
     *
     * @param handler handler.
     * @return dispatcher which used to stop streaming.
     */
    public Shutdownable user(Handler handler) {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.invokeLater(() -> {
            while (true) {
                Response response = client.get("streaming/user", null);
                if (!response.isSuccessful()) {
                    errorHandler.accept(response);
                    throw new RuntimeException(new Mastodon4jRequestException(response));
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
                    try {
                        String line = reader.readLine();
                        if (line == null || line.isEmpty()) {
                            continue;
                        }
                        String type = line.split(":")[0].trim();
                        if (!"event".equals(type)) {
                            continue;
                        }
                        String event = line.split(":")[1].trim();
                        String payload = reader.readLine();
                        String payloadType = payload.split(":")[0].trim();
                        if (!"data".equals(payloadType)) {
                            continue;
                        }

                        int start = payload.indexOf(":") + 1;
                        String json = payload.substring(start).trim();
                        switch (event) {
                            case "update":
                                Status status = client.getSerializer().fromJson(json, Status.class);
                                handler.onStatus(status);
                                break;
                            case "notification":
                                Notification notification = client.getSerializer().fromJson(json, Notification.class);
                                handler.onNotification(notification);
                                break;
                            case "delete":
                                Long id = client.getSerializer().fromJson(json, Long.class);
                                handler.onDelete(id);
                                break;
                            default:
                                LOGGER.warn("Unknown event: " + event);
                        }
                    } catch (InterruptedIOException e) {
                        break;
                    }
                } catch (IOException e) {
                    LOGGER.error("Cannot read line from streaming.", e);
                    if (!retryable) {
                        LOGGER.error("exit.");
                        break;
                    }
                    LOGGER.error("retry.");
                }
            }
        });

        return new Shutdownable(dispatcher);
    }
}
