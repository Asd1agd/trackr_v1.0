package com.example.financetracker.`data`

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class TransactionDao_Impl(
  __db: RoomDatabase,
) : TransactionDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfTransaction: EntityInsertAdapter<Transaction>

  private val __deleteAdapterOfTransaction: EntityDeleteOrUpdateAdapter<Transaction>

  private val __updateAdapterOfTransaction: EntityDeleteOrUpdateAdapter<Transaction>
  init {
    this.__db = __db
    this.__insertAdapterOfTransaction = object : EntityInsertAdapter<Transaction>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `transactions` (`id`,`amount`,`type`,`timestamp`,`categoryId`,`note`,`isSubscription`,`dueDate`,`importId`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Transaction) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindDouble(2, entity.amount)
        statement.bindText(3, entity.type)
        statement.bindLong(4, entity.timestamp)
        val _tmpCategoryId: Int? = entity.categoryId
        if (_tmpCategoryId == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpCategoryId.toLong())
        }
        statement.bindText(6, entity.note)
        val _tmp: Int = if (entity.isSubscription) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        val _tmpDueDate: Long? = entity.dueDate
        if (_tmpDueDate == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpDueDate)
        }
        val _tmpImportId: Int? = entity.importId
        if (_tmpImportId == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpImportId.toLong())
        }
      }
    }
    this.__deleteAdapterOfTransaction = object : EntityDeleteOrUpdateAdapter<Transaction>() {
      protected override fun createQuery(): String = "DELETE FROM `transactions` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Transaction) {
        statement.bindLong(1, entity.id.toLong())
      }
    }
    this.__updateAdapterOfTransaction = object : EntityDeleteOrUpdateAdapter<Transaction>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `transactions` SET `id` = ?,`amount` = ?,`type` = ?,`timestamp` = ?,`categoryId` = ?,`note` = ?,`isSubscription` = ?,`dueDate` = ?,`importId` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Transaction) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindDouble(2, entity.amount)
        statement.bindText(3, entity.type)
        statement.bindLong(4, entity.timestamp)
        val _tmpCategoryId: Int? = entity.categoryId
        if (_tmpCategoryId == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpCategoryId.toLong())
        }
        statement.bindText(6, entity.note)
        val _tmp: Int = if (entity.isSubscription) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        val _tmpDueDate: Long? = entity.dueDate
        if (_tmpDueDate == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpDueDate)
        }
        val _tmpImportId: Int? = entity.importId
        if (_tmpImportId == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpImportId.toLong())
        }
        statement.bindLong(10, entity.id.toLong())
      }
    }
  }

  public override suspend fun insert(transaction: Transaction): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfTransaction.insert(_connection, transaction)
  }

  public override suspend fun delete(transaction: Transaction): Unit = performSuspending(__db,
      false, true) { _connection ->
    __deleteAdapterOfTransaction.handle(_connection, transaction)
  }

  public override suspend fun update(transaction: Transaction): Unit = performSuspending(__db,
      false, true) { _connection ->
    __updateAdapterOfTransaction.handle(_connection, transaction)
  }

  public override fun getAllTransactions(): Flow<List<Transaction>> {
    val _sql: String = "SELECT * FROM transactions ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("transactions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfCategoryId: Int = getColumnIndexOrThrow(_stmt, "categoryId")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfIsSubscription: Int = getColumnIndexOrThrow(_stmt, "isSubscription")
        val _columnIndexOfDueDate: Int = getColumnIndexOrThrow(_stmt, "dueDate")
        val _columnIndexOfImportId: Int = getColumnIndexOrThrow(_stmt, "importId")
        val _result: MutableList<Transaction> = mutableListOf()
        while (_stmt.step()) {
          val _item: Transaction
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpCategoryId: Int?
          if (_stmt.isNull(_columnIndexOfCategoryId)) {
            _tmpCategoryId = null
          } else {
            _tmpCategoryId = _stmt.getLong(_columnIndexOfCategoryId).toInt()
          }
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpIsSubscription: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsSubscription).toInt()
          _tmpIsSubscription = _tmp != 0
          val _tmpDueDate: Long?
          if (_stmt.isNull(_columnIndexOfDueDate)) {
            _tmpDueDate = null
          } else {
            _tmpDueDate = _stmt.getLong(_columnIndexOfDueDate)
          }
          val _tmpImportId: Int?
          if (_stmt.isNull(_columnIndexOfImportId)) {
            _tmpImportId = null
          } else {
            _tmpImportId = _stmt.getLong(_columnIndexOfImportId).toInt()
          }
          _item =
              Transaction(_tmpId,_tmpAmount,_tmpType,_tmpTimestamp,_tmpCategoryId,_tmpNote,_tmpIsSubscription,_tmpDueDate,_tmpImportId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTransactionsBetween(startDate: Long, endDate: Long):
      Flow<List<Transaction>> {
    val _sql: String =
        "SELECT * FROM transactions WHERE timestamp >= ? AND timestamp <= ? ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("transactions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, startDate)
        _argIndex = 2
        _stmt.bindLong(_argIndex, endDate)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfCategoryId: Int = getColumnIndexOrThrow(_stmt, "categoryId")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfIsSubscription: Int = getColumnIndexOrThrow(_stmt, "isSubscription")
        val _columnIndexOfDueDate: Int = getColumnIndexOrThrow(_stmt, "dueDate")
        val _columnIndexOfImportId: Int = getColumnIndexOrThrow(_stmt, "importId")
        val _result: MutableList<Transaction> = mutableListOf()
        while (_stmt.step()) {
          val _item: Transaction
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpCategoryId: Int?
          if (_stmt.isNull(_columnIndexOfCategoryId)) {
            _tmpCategoryId = null
          } else {
            _tmpCategoryId = _stmt.getLong(_columnIndexOfCategoryId).toInt()
          }
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpIsSubscription: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsSubscription).toInt()
          _tmpIsSubscription = _tmp != 0
          val _tmpDueDate: Long?
          if (_stmt.isNull(_columnIndexOfDueDate)) {
            _tmpDueDate = null
          } else {
            _tmpDueDate = _stmt.getLong(_columnIndexOfDueDate)
          }
          val _tmpImportId: Int?
          if (_stmt.isNull(_columnIndexOfImportId)) {
            _tmpImportId = null
          } else {
            _tmpImportId = _stmt.getLong(_columnIndexOfImportId).toInt()
          }
          _item =
              Transaction(_tmpId,_tmpAmount,_tmpType,_tmpTimestamp,_tmpCategoryId,_tmpNote,_tmpIsSubscription,_tmpDueDate,_tmpImportId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getRecentDuplicate(
    amount: Double,
    type: String,
    timeLimit: Long,
  ): Transaction? {
    val _sql: String =
        "SELECT * FROM transactions WHERE amount = ? AND type = ? AND timestamp >= ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindDouble(_argIndex, amount)
        _argIndex = 2
        _stmt.bindText(_argIndex, type)
        _argIndex = 3
        _stmt.bindLong(_argIndex, timeLimit)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfCategoryId: Int = getColumnIndexOrThrow(_stmt, "categoryId")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfIsSubscription: Int = getColumnIndexOrThrow(_stmt, "isSubscription")
        val _columnIndexOfDueDate: Int = getColumnIndexOrThrow(_stmt, "dueDate")
        val _columnIndexOfImportId: Int = getColumnIndexOrThrow(_stmt, "importId")
        val _result: Transaction?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpCategoryId: Int?
          if (_stmt.isNull(_columnIndexOfCategoryId)) {
            _tmpCategoryId = null
          } else {
            _tmpCategoryId = _stmt.getLong(_columnIndexOfCategoryId).toInt()
          }
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpIsSubscription: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsSubscription).toInt()
          _tmpIsSubscription = _tmp != 0
          val _tmpDueDate: Long?
          if (_stmt.isNull(_columnIndexOfDueDate)) {
            _tmpDueDate = null
          } else {
            _tmpDueDate = _stmt.getLong(_columnIndexOfDueDate)
          }
          val _tmpImportId: Int?
          if (_stmt.isNull(_columnIndexOfImportId)) {
            _tmpImportId = null
          } else {
            _tmpImportId = _stmt.getLong(_columnIndexOfImportId).toInt()
          }
          _result =
              Transaction(_tmpId,_tmpAmount,_tmpType,_tmpTimestamp,_tmpCategoryId,_tmpNote,_tmpIsSubscription,_tmpDueDate,_tmpImportId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTransactionsSinceSync(startDate: Long): List<Transaction> {
    val _sql: String = "SELECT * FROM transactions WHERE timestamp >= ? ORDER BY timestamp DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, startDate)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfCategoryId: Int = getColumnIndexOrThrow(_stmt, "categoryId")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfIsSubscription: Int = getColumnIndexOrThrow(_stmt, "isSubscription")
        val _columnIndexOfDueDate: Int = getColumnIndexOrThrow(_stmt, "dueDate")
        val _columnIndexOfImportId: Int = getColumnIndexOrThrow(_stmt, "importId")
        val _result: MutableList<Transaction> = mutableListOf()
        while (_stmt.step()) {
          val _item: Transaction
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpCategoryId: Int?
          if (_stmt.isNull(_columnIndexOfCategoryId)) {
            _tmpCategoryId = null
          } else {
            _tmpCategoryId = _stmt.getLong(_columnIndexOfCategoryId).toInt()
          }
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpIsSubscription: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsSubscription).toInt()
          _tmpIsSubscription = _tmp != 0
          val _tmpDueDate: Long?
          if (_stmt.isNull(_columnIndexOfDueDate)) {
            _tmpDueDate = null
          } else {
            _tmpDueDate = _stmt.getLong(_columnIndexOfDueDate)
          }
          val _tmpImportId: Int?
          if (_stmt.isNull(_columnIndexOfImportId)) {
            _tmpImportId = null
          } else {
            _tmpImportId = _stmt.getLong(_columnIndexOfImportId).toInt()
          }
          _item =
              Transaction(_tmpId,_tmpAmount,_tmpType,_tmpTimestamp,_tmpCategoryId,_tmpNote,_tmpIsSubscription,_tmpDueDate,_tmpImportId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteByImportId(importId: Int) {
    val _sql: String = "DELETE FROM transactions WHERE importId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, importId.toLong())
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
