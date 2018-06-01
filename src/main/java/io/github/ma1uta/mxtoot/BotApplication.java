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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.sslreload.SslReloadBundle;
import io.github.ma1uta.jeon.exception.ExceptionHandler;
import io.github.ma1uta.mxtoot.matrix.AppResource;
import io.github.ma1uta.mxtoot.matrix.MxTootBotPool;
import io.github.ma1uta.mxtoot.matrix.MxTootConfig;
import io.github.ma1uta.mxtoot.matrix.MxTootDao;
import io.github.ma1uta.mxtoot.matrix.MxTootPersistentService;
import io.github.ma1uta.mxtoot.matrix.MxTootTransaction;
import io.github.ma1uta.mxtoot.matrix.MxTootTransactionDao;

import javax.ws.rs.client.Client;

/**
 * Matrix bot.
 */
public class BotApplication extends Application<BotConfiguration> {

    private HibernateBundle<BotConfiguration> matrixHibernate = new HibernateBundle<BotConfiguration>(MxTootConfig.class,
        MxTootTransaction.class) {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(BotConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    /**
     * Entry point.
     *
     * @param args arguments.
     * @throws Exception never throws.
     */
    public static void main(String[] args) throws Exception {
        new BotApplication().run(args);
    }

    @Override
    public void run(BotConfiguration botConfiguration, Environment environment) {
        matrixBot(botConfiguration, environment);
    }

    @Override
    public void initialize(Bootstrap<BotConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
            new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));
        bootstrap.addBundle(new SslReloadBundle());

        bootstrap.getObjectMapper().enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);

        bootstrap.addBundle(matrixHibernate);
    }

    @SuppressWarnings("unchecked")
    private void matrixBot(BotConfiguration botConfiguration, Environment environment) {
        environment.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        environment.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, botConfiguration.isStrictMode());

        Client jersey = new JerseyClientBuilder(environment).using(botConfiguration.getJerseyClient()).build("jersey");

        UnitOfWorkAwareProxyFactory proxyFactory = new UnitOfWorkAwareProxyFactory(matrixHibernate);
        MxTootDao mxTootDao = new MxTootDao(matrixHibernate.getSessionFactory());
        MxTootTransactionDao mxTootTransactionDao = new MxTootTransactionDao(matrixHibernate.getSessionFactory());

        MxTootPersistentService<MxTootDao> botService = proxyFactory.create(MxTootPersistentService.class, Object.class, mxTootDao);
        MxTootPersistentService<MxTootTransactionDao> transactionService = proxyFactory.create(MxTootPersistentService.class, Object.class,
            mxTootTransactionDao);
        MxTootBotPool mxTootBotPool = new MxTootBotPool(botConfiguration, botService, jersey, botConfiguration.getCommands());

        environment.lifecycle().manage(mxTootBotPool);
        environment.jersey()
            .register(
                new AppResource(mxTootTransactionDao, mxTootBotPool, botConfiguration.getHsToken(), botConfiguration.getHomeserverUrl(),
                    botService, transactionService));
        environment.jersey().register(new ExceptionHandler());
    }
}
