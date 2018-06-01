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

import io.dropwizard.hibernate.AbstractDAO;
import io.github.ma1uta.matrix.bot.BotDao;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * DAO for matrix bot.
 */
public class MxTootDao extends AbstractDAO<MxTootConfig> implements BotDao<MxTootConfig> {

    /**
     * Creates a new DAO with a given session provider.
     *
     * @param sessionFactory a session provider
     */
    public MxTootDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Find all matrix bots.
     *
     * @return matrix bots.
     */
    @SuppressWarnings("unchecked")
    public List<MxTootConfig> findAll() {
        return list(namedQuery("matrix.bot.findAll"));
    }

    /**
     * Check that specified user exists.
     *
     * @param userId mxid.
     * @return {@code true} if user exists or {@code false}.
     */
    @SuppressWarnings("unchecked")
    public boolean user(String userId) {
        return uniqueResult(namedQuery("matrix.bot.findByUserId").setParameter("userId", userId)) != null;
    }

    /**
     * Save new bot's data.
     *
     * @param data bot's data.
     * @return saved entity.
     */
    public MxTootConfig save(MxTootConfig data) {
        return persist(data);
    }

    /**
     * Delete bot's data.
     *
     * @param data data to delete.
     */
    public void delete(MxTootConfig data) {
        currentSession().delete(data);
    }
}
