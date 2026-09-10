with open('app/src/main/java/com/example/financetracker/ui/GoalsScreen.kt', 'r') as f:
    content = f.read()

state_additions = """
    var showEditGoalDialog by remember { mutableStateOf<com.example.financetracker.data.Goal?>(null) }
    var editGoalName by remember { mutableStateOf("") }
    var editGoalTarget by remember { mutableStateOf("") }
    var editGoalCurrent by remember { mutableStateOf("") }
"""

# If they weren't added, add them now
if "var showEditGoalDialog" not in content:
    content = content.replace(
        'var showAddDialog by remember { mutableStateOf(false) }',
        'var showAddDialog by remember { mutableStateOf(false) }\n' + state_additions
    )

with open('app/src/main/java/com/example/financetracker/ui/GoalsScreen.kt', 'w') as f:
    f.write(content)
