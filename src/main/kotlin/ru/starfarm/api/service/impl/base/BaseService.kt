package ru.starfarm.api.service.impl.base

import org.intellij.lang.annotations.Language
import ru.starfarm.api.service.BukkitService
import ru.starfarm.api.service.impl.base.exception.BaseIsNotConnectedException
import ru.starfarm.api.service.impl.base.exception.TableIsNotConnected
import ru.starfarm.core.database.DatabaseApi
import ru.starfarm.core.database.DatabaseCredentials
import ru.starfarm.core.database.executor.factory.QueuedDatabaseExecuteHandlerFactory
import ru.starfarm.core.database.query.DatabaseResultSetWrapper
import ru.starfarm.core.database.table.DatabaseTable

class BaseService(host: String, database: String, user: String, password: String) : BukkitService {

    private val connectedBase = DatabaseApi.createConnection(
        DatabaseCredentials(user, host, password, database),
        QueuedDatabaseExecuteHandlerFactory
    )

    private val isClosed get() = connectedBase.connection.isClosed
    private val executeHandler get() = connectedBase.executeHandler

    override fun load() {
        connectedBase.executeHandler.debug = true

        logger.info("Connected to the base.")
    }

    fun query(
        @Language("sql") query: String,
        vararg values: Any?,
        resultConsumer: ((DatabaseResultSetWrapper) -> Unit)? = null
    ) {
        if (isClosed) throw BaseIsNotConnectedException()

        executeHandler.queryAsync(query, *values).thenAccept(resultConsumer)
    }

    fun updateQuery(@Language("sql") query: String, vararg values: Any?) {
        if (isClosed) throw BaseIsNotConnectedException()

        executeHandler.updateAsync(query, *values)
    }

    fun getTable(tableName: String): DatabaseTable {
        if (isClosed) throw BaseIsNotConnectedException()

        return kotlin.runCatching { connectedBase.getTable(tableName) }.getOrElse { throw TableIsNotConnected() }
    }
}