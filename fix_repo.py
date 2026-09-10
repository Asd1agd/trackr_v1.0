with open('app/src/main/java/com/example/financetracker/repository/FinanceRepository.kt', 'r') as f:
    content = f.read()

bad_imports = """
import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

"""
content = content.replace(bad_imports, '')
content = content.replace(
    'package com.example.financetracker.repository',
    'package com.example.financetracker.repository\n' + bad_imports
)

with open('app/src/main/java/com/example/financetracker/repository/FinanceRepository.kt', 'w') as f:
    f.write(content)
