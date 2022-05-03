package io.github.nickacpt.stargazer.db

import com.kotlindiscord.kord.extensions.utils.env
import io.github.nickacpt.stargazer.db.tables.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseHelper {

    lateinit var db: Database

    fun initDatabase() {
        db = Database.connect(
            "jdbc:postgresql://localhost:5432/${env("DB_NAME")}",
            driver = "org.postgresql.Driver",
            user = env("DB_USER"),
            password = env("DB_PASS")
        )

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.createMissingTablesAndColumns(
                Messages, MessageAttachments, CrawlLogs, Users, MessageStarReactions, Channels
            )
        }
    }

}