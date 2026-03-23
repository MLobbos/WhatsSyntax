package com.whatssyntax.api.data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {

    fun init(application: Application) {
        val config = application.environment.config
        val jdbcUrl = config.property("database.url").getString()
        val driver = config.property("database.driver").getString()
        val maxPoolSize = config.propertyOrNull("database.maxPoolSize")
            ?.getString()?.toIntOrNull() ?: 10

        val dataSource = HikariDataSource(HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            this.driverClassName = driver
            this.maximumPoolSize = maxPoolSize
            this.isAutoCommit = false
            this.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        })

        // Run Flyway migrations
        Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .load()
            .migrate()

        Database.connect(dataSource)
        application.environment.log.info("Database connected and migrations applied")
    }
}
