package com.example.noteapp.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize as Parcelize

@Parcelize
@Entity(tableName = "notes")
data class Note(
        @PrimaryKey(autoGenerate = true)
        val id: Long,

        val title: String,

        @ColumnInfo(name = "date_time")
        val dateTime: String,

        val subtitle: String,

        val text: String,

        val color: String?,

        @ColumnInfo(name = "image_path")
        val imagePath: String?,

        @ColumnInfo(name = "web_link")
        val webLink: String?,

        val video: String?,

        val audio: String?,
): Parcelable
