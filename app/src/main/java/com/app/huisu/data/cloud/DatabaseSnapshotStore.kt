package com.app.huisu.data.cloud

import android.database.Cursor
import android.util.Base64
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.huisu.data.database.AppDatabase
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSnapshotStore @Inject constructor(
    private val database: AppDatabase
) {
    fun exportSnapshot(): JSONObject {
        val tables = JSONObject()
        val db = database.openHelper.readableDatabase

        SYNC_TABLES.forEach { table ->
            db.query("SELECT * FROM `${quote(table)}`").use { cursor ->
                tables.put(table, cursor.toJsonArray())
            }
        }

        return JSONObject()
            .put("schemaVersion", SNAPSHOT_SCHEMA_VERSION)
            .put("exportedAt", System.currentTimeMillis())
            .put("tables", tables)
    }

    fun importSnapshot(snapshot: JSONObject) {
        val tables = snapshot.optJSONObject("tables") ?: return
        val db = database.openHelper.writableDatabase

        db.beginTransaction()
        try {
            SYNC_TABLES.forEach { table ->
                val rows = tables.optJSONArray(table) ?: return@forEach
                for (index in 0 until rows.length()) {
                    val row = rows.optJSONObject(index) ?: continue
                    db.insertOrReplace(table, row)
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun Cursor.toJsonArray(): JSONArray {
        val rows = JSONArray()
        val columns = columnNames

        while (moveToNext()) {
            val row = JSONObject()
            columns.forEachIndexed { index, column ->
                when (getType(index)) {
                    Cursor.FIELD_TYPE_NULL -> row.put(column, JSONObject.NULL)
                    Cursor.FIELD_TYPE_INTEGER -> row.put(column, getLong(index))
                    Cursor.FIELD_TYPE_FLOAT -> row.put(column, getDouble(index))
                    Cursor.FIELD_TYPE_STRING -> row.put(column, getString(index))
                    Cursor.FIELD_TYPE_BLOB -> row.put(
                        column,
                        Base64.encodeToString(getBlob(index), Base64.NO_WRAP)
                    )
                }
            }
            rows.put(row)
        }
        return rows
    }

    private fun SupportSQLiteDatabase.insertOrReplace(table: String, row: JSONObject) {
        val columns = row.keys().asSequence().toList()
        if (columns.isEmpty()) return

        val placeholders = columns.joinToString(", ") { "?" }
        val columnSql = columns.joinToString(", ") { "`${quote(it)}`" }
        val statement = compileStatement(
            "INSERT OR REPLACE INTO `${quote(table)}` ($columnSql) VALUES ($placeholders)"
        )

        columns.forEachIndexed { index, column ->
            val bindIndex = index + 1
            when (val value = row.opt(column)) {
                null, JSONObject.NULL -> statement.bindNull(bindIndex)
                is Int -> statement.bindLong(bindIndex, value.toLong())
                is Long -> statement.bindLong(bindIndex, value)
                is Boolean -> statement.bindLong(bindIndex, if (value) 1L else 0L)
                is Float -> statement.bindDouble(bindIndex, value.toDouble())
                is Double -> statement.bindDouble(bindIndex, value)
                is Number -> statement.bindDouble(bindIndex, value.toDouble())
                else -> statement.bindString(bindIndex, value.toString())
            }
        }
        statement.executeInsert()
        statement.close()
    }

    private fun quote(value: String): String = value.replace("`", "``")

    companion object {
        private const val SNAPSHOT_SCHEMA_VERSION = 1

        private val SYNC_TABLES = listOf(
            "meditation_records",
            "affirmation_records",
            "affirmations",
            "video_links",
            "achievements",
            "todo_categories",
            "todo_items",
            "quick_notes"
        )
    }
}
