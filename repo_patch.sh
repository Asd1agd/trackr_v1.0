cat << 'INNER' >> app/src/main/java/com/example/financetracker/repository/FinanceRepository.kt

    val allImportLogs = importLogDao.getAllImportLogs()

    suspend fun insertImportLog(filename: String, timestamp: Long): Int {
        val log = com.example.financetracker.data.ImportLog(filename = filename, timestamp = timestamp)
        return importLogDao.insert(log).toInt()
    }

    suspend fun deleteImportLog(id: Int) {
        transactionDao.deleteByImportId(id)
        goalDao.deleteByImportId(id)
        budgetDao.deleteByImportId(id)
        importLogDao.delete(id)
        updateWidget()
    }
INNER
