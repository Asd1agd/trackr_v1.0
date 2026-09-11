package com.example.financetracker.`data`

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ImportLogDao_Impl(
  __db: RoomDatabase,
) : ImportLogDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfImportLog: EntityInsertAdapter<ImportLog>
  init {
    this.__db = __db
    this.__insertAdapterOfImportLog = object : EntityInsertAdapter<ImportLog>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `import_logs` (`id`,`filename`,`timestamp`) VALUES (nullif(?, 0),?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ImportLog) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.filename)
        statement.bindLong(3, entity.timestamp)
      }
    }
  }

  public override suspend fun insert(importLog: ImportLog): Long = performSuspending(__db, false,
      true) { _connection ->
    val _result: Long = __insertAdapterOfImportLog.insertAndReturnId(_connection, importLog)
    _result
  }

  public override fun getAllImportLogs(): Flow<List<ImportLog>> {
    val _sql: String = "SELECT * FROM import_logs ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("import_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfFilename: Int = getColumnIndexOrThrow(_stmt, "filename")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<ImportLog> = mutableListOf()
        while (_stmt.step()) {
          val _item: ImportLog
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpFilename: String
          _tmpFilename = _stmt.getText(_columnIndexOfFilename)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item = ImportLog(_tmpId,_tmpFilename,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun delete(id: Int) {
    val _sql: String = "DELETE FROM import_logs WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id.toLong())
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
