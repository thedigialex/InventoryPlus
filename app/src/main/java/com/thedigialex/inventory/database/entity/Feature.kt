package com.thedigialex.inventory.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "features",
    foreignKeys = [ForeignKey(
        entity = Project::class,
        parentColumns = ["id"],
        childColumns = ["projectId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("projectId")]
)
data class Feature(
    @PrimaryKey(autoGenerate = true) override val id: Int = 0,
    val projectId: Int,
    override val name: String,
    override val description: String
) : NamedItem
