package io.github.nickacpt.stargazer.db.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object CrawlLogs : LongIdTable() {
    val time = timestamp("time").clientDefault { Instant.now() }
    val totalMessages = integer("total_messages")
    val crawlContent = enumeration<CrawlContent>("crawl_content")
}