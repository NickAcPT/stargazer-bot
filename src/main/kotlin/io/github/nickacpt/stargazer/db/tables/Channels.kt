package io.github.nickacpt.stargazer.db.tables

import org.jetbrains.exposed.sql.Table

object Channels : Table() {
    val snowflakeId = long("snowflake")
    val name = text("name")

    override val primaryKey = PrimaryKey(snowflakeId)
}