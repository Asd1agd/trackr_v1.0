package com.example.financetracker.`data`

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Int
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
public class BudgetDao_Impl(
  __db: RoomDatabase,
) : BudgetDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBudget: EntityInsertAdapter<Budget>

  private val __updateAdapterOfBudget: EntityDeleteOrUpdateAdapter<Budget>
  init {
    this.__db = __db
    this.__insertAdapterOfBudget = object : EntityInsertAdapter<Budget>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `budgets` (`id`,`categoryId`,`amount`,`month`,`year`,`importId`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Budget) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.categoryId.toLong())
        statement.bindDouble(3, entity.amount)
        statement.bindLong(4, entity.month.toLong())
        statement.bindLong(5, entity.year.toLong())
        val _tmpImportId: Int? = entity.importId
        if (_tmpImportId == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpImportId.toLong())
        }
      }
    }
    this.__updateAdapterOfBudget = object : EntityDeleteOrUpdateAdapter<Budget>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `budgets` SET `id` = ?,`categoryId` = ?,`amount` = ?,`month` = ?,`year` = ?,`importId` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Budget) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.categoryId.toLong())
        statement.bindDouble(3, entity.amount)
        statement.bindLong(4, entity.month.toLong())
        statement.bindLong(5, entity.year.toLong())
        val _tmpImportId: Int? = entity.importId
        if (_tmpImportId == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpImportId.toLong())
        }
        statement.bindLong(7, entity.id.toLong())
      }
    }
  }

  public override suspend fun insert(budget: Budget): Unit = performSuspending(__db, false, true) {
      _connection ->
    __insertAdapterOfBudget.insert(_connection, budget)
  }

  public override suspend fun update(budget: Budget): Unit = performSuspending(__db, false, true) {
      _connection ->
    __updateAdapterOfBudget.handle(_connection, budget)
  }

  public override fun getAllBudgets(): Flow<List<Budget>> {
    val _sql: String = "SELECT * FROM budgets"
    return createFlow(__db, false, arrayOf("budgets")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCategoryId: Int = getColumnIndexOrThrow(_stmt, "categoryId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfMonth: Int = getColumnIndexOrThrow(_stmt, "month")
        val _columnIndexOfYear: Int = getColumnIndexOrThrow(_stmt, "year")
        val _columnIndexOfImportId: Int = getColumnIndexOrThrow(_stmt, "importId")
        val _result: MutableList<Budget> = mutableListOf()
        while (_stmt.step()) {
          val _item: Budget
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpCategoryId: Int
          _tmpCategoryId = _stmt.getLong(_columnIndexOfCategoryId).toInt()
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpMonth: Int
          _tmpMonth = _stmt.getLong(_columnIndexOfMonth).toInt()
          val _tmpYear: Int
          _tmpYear = _stmt.getLong(_columnIndexOfYear).toInt()
          val _tmpImportId: Int?
          if (_stmt.isNull(_columnIndexOfImportId)) {
            _tmpImportId = null
          } else {
            _tmpImportId = _stmt.getLong(_columnIndexOfImportId).toInt()
          }
          _item = Budget(_tmpId,_tmpCategoryId,_tmpAmount,_tmpMonth,_tmpYear,_tmpImportId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getBudgetsForMonth(month: Int, year: Int): Flow<List<Budget>> {
    val _sql: String = "SELECT * FROM budgets WHERE month = ? AND year = ?"
    return createFlow(__db, false, arrayOf("budgets")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, month.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, year.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCategoryId: Int = getColumnIndexOrThrow(_stmt, "categoryId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfMonth: Int = getColumnIndexOrThrow(_stmt, "month")
        val _columnIndexOfYear: Int = getColumnIndexOrThrow(_stmt, "year")
        val _columnIndexOfImportId: Int = getColumnIndexOrThrow(_stmt, "importId")
        val _result: MutableList<Budget> = mutableListOf()
        while (_stmt.step()) {
          val _item: Budget
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpCategoryId: Int
          _tmpCategoryId = _stmt.getLong(_columnIndexOfCategoryId).toInt()
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpMonth: Int
          _tmpMonth = _stmt.getLong(_columnIndexOfMonth).toInt()
          val _tmpYear: Int
          _tmpYear = _stmt.getLong(_columnIndexOfYear).toInt()
          val _tmpImportId: Int?
          if (_stmt.isNull(_columnIndexOfImportId)) {
            _tmpImportId = null
          } else {
            _tmpImportId = _stmt.getLong(_columnIndexOfImportId).toInt()
          }
          _item = Budget(_tmpId,_tmpCategoryId,_tmpAmount,_tmpMonth,_tmpYear,_tmpImportId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getBudget(
    categoryId: Int,
    month: Int,
    year: Int,
  ): Budget? {
    val _sql: String =
        "SELECT * FROM budgets WHERE categoryId = ? AND month = ? AND year = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, categoryId.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, month.toLong())
        _argIndex = 3
        _stmt.bindLong(_argIndex, year.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCategoryId: Int = getColumnIndexOrThrow(_stmt, "categoryId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfMonth: Int = getColumnIndexOrThrow(_stmt, "month")
        val _columnIndexOfYear: Int = getColumnIndexOrThrow(_stmt, "year")
        val _columnIndexOfImportId: Int = getColumnIndexOrThrow(_stmt, "importId")
        val _result: Budget?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpCategoryId: Int
          _tmpCategoryId = _stmt.getLong(_columnIndexOfCategoryId).toInt()
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpMonth: Int
          _tmpMonth = _stmt.getLong(_columnIndexOfMonth).toInt()
          val _tmpYear: Int
          _tmpYear = _stmt.getLong(_columnIndexOfYear).toInt()
          val _tmpImportId: Int?
          if (_stmt.isNull(_columnIndexOfImportId)) {
            _tmpImportId = null
          } else {
            _tmpImportId = _stmt.getLong(_columnIndexOfImportId).toInt()
          }
          _result = Budget(_tmpId,_tmpCategoryId,_tmpAmount,_tmpMonth,_tmpYear,_tmpImportId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getBudgetsForMonthSync(month: Int, year: Int): List<Budget> {
    val _sql: String = "SELECT * FROM budgets WHERE month = ? AND year = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, month.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, year.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCategoryId: Int = getColumnIndexOrThrow(_stmt, "categoryId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfMonth: Int = getColumnIndexOrThrow(_stmt, "month")
        val _columnIndexOfYear: Int = getColumnIndexOrThrow(_stmt, "year")
        val _columnIndexOfImportId: Int = getColumnIndexOrThrow(_stmt, "importId")
        val _result: MutableList<Budget> = mutableListOf()
        while (_stmt.step()) {
          val _item: Budget
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpCategoryId: Int
          _tmpCategoryId = _stmt.getLong(_columnIndexOfCategoryId).toInt()
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpMonth: Int
          _tmpMonth = _stmt.getLong(_columnIndexOfMonth).toInt()
          val _tmpYear: Int
          _tmpYear = _stmt.getLong(_columnIndexOfYear).toInt()
          val _tmpImportId: Int?
          if (_stmt.isNull(_columnIndexOfImportId)) {
            _tmpImportId = null
          } else {
            _tmpImportId = _stmt.getLong(_columnIndexOfImportId).toInt()
          }
          _item = Budget(_tmpId,_tmpCategoryId,_tmpAmount,_tmpMonth,_tmpYear,_tmpImportId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteByImportId(importId: Int) {
    val _sql: String = "DELETE FROM budgets WHERE importId = ?"
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
