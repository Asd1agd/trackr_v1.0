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
public class GoalDao_Impl(
  __db: RoomDatabase,
) : GoalDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfGoal: EntityInsertAdapter<Goal>

  private val __updateAdapterOfGoal: EntityDeleteOrUpdateAdapter<Goal>
  init {
    this.__db = __db
    this.__insertAdapterOfGoal = object : EntityInsertAdapter<Goal>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `goals` (`id`,`name`,`targetAmount`,`currentAmount`,`targetDate`,`importId`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Goal) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.name)
        statement.bindDouble(3, entity.targetAmount)
        statement.bindDouble(4, entity.currentAmount)
        statement.bindLong(5, entity.targetDate)
        val _tmpImportId: Int? = entity.importId
        if (_tmpImportId == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpImportId.toLong())
        }
      }
    }
    this.__updateAdapterOfGoal = object : EntityDeleteOrUpdateAdapter<Goal>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `goals` SET `id` = ?,`name` = ?,`targetAmount` = ?,`currentAmount` = ?,`targetDate` = ?,`importId` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Goal) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.name)
        statement.bindDouble(3, entity.targetAmount)
        statement.bindDouble(4, entity.currentAmount)
        statement.bindLong(5, entity.targetDate)
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

  public override suspend fun insert(goal: Goal): Unit = performSuspending(__db, false, true) {
      _connection ->
    __insertAdapterOfGoal.insert(_connection, goal)
  }

  public override suspend fun update(goal: Goal): Unit = performSuspending(__db, false, true) {
      _connection ->
    __updateAdapterOfGoal.handle(_connection, goal)
  }

  public override fun getAllGoals(): Flow<List<Goal>> {
    val _sql: String = "SELECT * FROM goals"
    return createFlow(__db, false, arrayOf("goals")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfTargetAmount: Int = getColumnIndexOrThrow(_stmt, "targetAmount")
        val _columnIndexOfCurrentAmount: Int = getColumnIndexOrThrow(_stmt, "currentAmount")
        val _columnIndexOfTargetDate: Int = getColumnIndexOrThrow(_stmt, "targetDate")
        val _columnIndexOfImportId: Int = getColumnIndexOrThrow(_stmt, "importId")
        val _result: MutableList<Goal> = mutableListOf()
        while (_stmt.step()) {
          val _item: Goal
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpTargetAmount: Double
          _tmpTargetAmount = _stmt.getDouble(_columnIndexOfTargetAmount)
          val _tmpCurrentAmount: Double
          _tmpCurrentAmount = _stmt.getDouble(_columnIndexOfCurrentAmount)
          val _tmpTargetDate: Long
          _tmpTargetDate = _stmt.getLong(_columnIndexOfTargetDate)
          val _tmpImportId: Int?
          if (_stmt.isNull(_columnIndexOfImportId)) {
            _tmpImportId = null
          } else {
            _tmpImportId = _stmt.getLong(_columnIndexOfImportId).toInt()
          }
          _item =
              Goal(_tmpId,_tmpName,_tmpTargetAmount,_tmpCurrentAmount,_tmpTargetDate,_tmpImportId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteByImportId(importId: Int) {
    val _sql: String = "DELETE FROM goals WHERE importId = ?"
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

  public override suspend fun deleteById(id: Int) {
    val _sql: String = "DELETE FROM goals WHERE id = ?"
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
