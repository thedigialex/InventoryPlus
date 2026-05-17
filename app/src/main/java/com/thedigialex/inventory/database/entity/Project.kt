package com.thedigialex.inventory.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) override val id: Int = 0,
    override val name: String,
    override val description: String,
    val createdDate: Long = System.currentTimeMillis()
) : NamedItem
