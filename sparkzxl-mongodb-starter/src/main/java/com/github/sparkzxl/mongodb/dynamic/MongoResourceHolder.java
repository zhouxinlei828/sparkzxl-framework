package com.github.sparkzxl.mongodb.dynamic;

import com.mongodb.client.ClientSession;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.ResourceHolderSupport;

public class MongoResourceHolder extends ResourceHolderSupport {

    private @Nullable
    ClientSession session;
    private MongoDatabaseFactory dbFactory;


    MongoResourceHolder(@Nullable ClientSession session, MongoDatabaseFactory dbFactory) {

        this.session = session;
        this.dbFactory = dbFactory;
    }

    @Nullable
    ClientSession getSession() {
        return session;
    }

    ClientSession getRequiredSession() {

        ClientSession session = getSession();

        if (session == null) {
            throw new IllegalStateException("No session available!");
        }

        return session;
    }

    public MongoDatabaseFactory getDbFactory() {
        return dbFactory;
    }

    public void setSession(@Nullable ClientSession session) {
        this.session = session;
    }

    void setTimeoutIfNotDefaulted(int seconds) {

        if (seconds != TransactionDefinition.TIMEOUT_DEFAULT) {
            setTimeoutInSeconds(seconds);
        }
    }

    /**
     * @return {@literal true} if session is not {@literal null}.
     */
    boolean hasSession() {
        return session != null;
    }

    /**
     * @return {@literal true} if the session is active and has not been closed.
     */
    boolean hasActiveSession() {

        if (!hasSession()) {
            return false;
        }

        return hasServerSession() && !getRequiredSession().getServerSession().isClosed();
    }

    /**
     * @return {@literal true} if the session has an active transaction.
     * @since 2.1.3
     * @see #hasActiveSession()
     */
    boolean hasActiveTransaction() {

        if (!hasActiveSession()) {
            return false;
        }

        return getRequiredSession().hasActiveTransaction();
    }

    /**
     * @return {@literal true} if the {@link ClientSession} has a {@link com.mongodb.session.ServerSession} associated
     *         that is accessible via {@link ClientSession#getServerSession()}.
     */
    boolean hasServerSession() {

        try {
            return getRequiredSession().getServerSession() != null;
        } catch (IllegalStateException serverSessionClosed) {
            // ignore
        }

        return false;
    }

}