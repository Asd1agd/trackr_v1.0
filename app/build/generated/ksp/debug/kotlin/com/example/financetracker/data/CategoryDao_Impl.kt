package com.example.financetracker.`data`

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
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
public class CategoryDao_Impl(
  __db: RoomDatabase,
) : CategoryDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCategory: EntityInsertAdapter<Category>
  init {
    this.__db = __db
    this.__insertAdapterOfCategory = object : EntityInsertAdapter<Category>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `categories` (`id`,`name`,`keywords`,`isEssential`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Category) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.keywords)
        val _tmp: Int = if (entity.isEssential) 1 else 0
        statement.bindLong(4, _tmp.toLong())
      }
    }
  }

  public override suspend fun insert(category: Category): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfCategory.insert(_connection, category)
  }

  public override fun getAllCategories(): Flow<List<Category>> {
    val _sql: String = "SELECT * FROM categories"
    return createFlow(__db, false, arrayOf("categories")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfKeywords: Int = getColumnIndexOrThrow(_stmt, "keywords")
        val _columnIndexOfIsEssential: Int = getColumnIndexOrThrow(_stmt, "isEssential")
        val _result: MutableList<Category> = mutableListOf()
        while (_stmt.step()) {
          val _item: Category
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpKeywords: String
          _tmpKeywords = _stmt.getText(_columnIndexOfKeywords)
          val _tmpIsEssential: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsEssential).toInt()
          _tmpIsEssential = _tmp != 0
          _item = Category(_tmpId,_tmpName,_tmpKeywords,_tmpIsEssential)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCategoriesSync(): List<Category> {
    val _sql: String = "SELECT * FROM categories"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfKeywords: Int = getColumnIndexOrThrow(_stmt, "keywords")
        val _columnIndexOfIsEssential: Int = getColumnIndexOrThrow(_stmt, "isEssential")
        val _result: MutableList<Category> = mutableListOf()
        while (_stmt.step()) {
          val _item: Category
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpKeywords: String
          _tmpKeywords = _stmt.getText(_columnIndexOfKeywords)
          val _tmpIsEssential: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsEssential).toInt()
          _tmpIsEssential = _tmp != 0
          _item = Category(_tmpId,_tmpName,_tmpKeywords,_tmpIsEssential)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
