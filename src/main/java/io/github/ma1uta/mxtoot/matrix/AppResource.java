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

import io.github.ma1uta.jeon.exception.MatrixException;
import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.ErrorResponse;
import io.github.ma1uta.matrix.application.api.ApplicationApi;
import io.github.ma1uta.matrix.application.model.TransactionRequest;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Application REST service.
 */
public class AppResource implements ApplicationApi {

    private final MxTootTransactionDao mxTootTransactionDao;
    private final MxTootBotPool mxTootBotPool;
    private final MxTootPersistentService<MxTootDao> botService;
    private final MxTootPersistentService<MxTootTransactionDao> transactionService;
    private final String hsToken;
    private final String url;

    public AppResource(MxTootTransactionDao mxTootTransactionDao, MxTootBotPool mxTootBotPool, String hsToken,
                       String url, MxTootPersistentService<MxTootDao> botService,
                       MxTootPersistentService<MxTootTransactionDao> transactionService) {
        this.mxTootTransactionDao = mxTootTransactionDao;
        this.mxTootBotPool = mxTootBotPool;
        this.hsToken = hsToken;
        this.url = url;
        this.botService = botService;
        this.transactionService = transactionService;
    }

    public String getHsToken() {
        return hsToken;
    }

    public String getUrl() {
        return url;
    }

    public MxTootBotPool getMxTootBotPool() {
        return mxTootBotPool;
    }

    public MxTootTransactionDao getMxTootTransactionDao() {
        return mxTootTransactionDao;
    }

    public MxTootPersistentService<MxTootDao> getBotService() {
        return botService;
    }

    public MxTootPersistentService<MxTootTransactionDao> getTransactionService() {
        return transactionService;
    }

    @Override
    public EmptyResponse transaction(String txnId, TransactionRequest request, HttpServletRequest servletRequest,
                                     HttpServletResponse servletResponse) {
        validateAsToken(servletRequest);

        if (!getTransactionService().invoke(dao -> {
            return dao.exist(txnId);
        })) {
            Optional<Boolean> result = request.getEvents().stream().map(event -> getMxTootBotPool().send(event))
                .filter(Boolean::booleanValue).findAny();
            if (result.isPresent() && result.get()) {
                getTransactionService().invoke((dao) -> {
                    MxTootTransaction transaction = new MxTootTransaction();
                    transaction.setId(txnId);
                    transaction.setProcessed(LocalDateTime.now());
                    getMxTootTransactionDao().save(transaction);
                });
            } else {
                throw new MatrixException(ErrorResponse.Code.M_NOT_FOUND, "Bot not found", HttpServletResponse.SC_NOT_FOUND);
            }
        }

        return new EmptyResponse();
    }

    @Override
    public EmptyResponse rooms(String roomAlias, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        throw new MatrixException(getUrl().toUpperCase() + "_NOT_FOUND", "", HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    public EmptyResponse users(String userId, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        validateAsToken(servletRequest);
        if (getBotService().invoke((dao) -> {
            return dao.user(userId);
        })) {
            throw new MatrixException(ErrorResponse.Code.M_USER_IN_USE, "User has been already registred", HttpServletResponse.SC_CONFLICT);
        } else {
            getMxTootBotPool().startNewBot(userId);
            return new EmptyResponse();
        }
    }

    protected void validateAsToken(HttpServletRequest servletRequest) {
        String asToken = servletRequest.getParameter("access_token");
        if (StringUtils.isBlank(asToken)) {
            throw new MatrixException(getUrl().toUpperCase() + "_UNAUTHORIZED", "", HttpServletResponse.SC_UNAUTHORIZED);
        }

        if (!getHsToken().equals(asToken)) {
            throw new MatrixException(ErrorResponse.Code.M_FORBIDDEN, "", HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
