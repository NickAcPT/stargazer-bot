package io.github.nickacpt.stargazer.db.tables

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = long("snowflake").autoIncrement()
    val username = varchar("username", 255)
    val discriminator = varchar("discriminator", 4)

    override val primaryKey = PrimaryKey(id)
}