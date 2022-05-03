package io.github.nickacpt.stargazer.db.tables

import com.kotlindiscord.kord.extensions.commands.application.slash.converters.ChoiceEnum

enum class CrawlContent(override val readableName: String) : ChoiceEnum {
    NOTHING("Nothing"),
    STARBOARD_LINKS("Starboard Links"),
    MESSAGES_LINKED_IN_STARBOARD("Messages Linked in Starboard"),
    STARRED_MESSAGES_AND_ATTACHMENTS("Starred Messages and Attachments"),
}
