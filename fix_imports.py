with open("app/src/main/java/com/example/financetracker/MainActivity.kt", "r") as f:
    lines = f.readlines()

new_imports = [
    "import androidx.compose.ui.input.pointer.pointerInput\n",
    "import androidx.compose.ui.input.pointer.PointerEventPass\n",
    "import androidx.compose.ui.input.pointer.changedToDown\n",
    "import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed\n"
]

out = []
for line in lines:
    out.append(line)
    if line.startswith("import androidx.compose.ui.Modifier"):
        for imp in new_imports:
            out.append(imp)

with open("app/src/main/java/com/example/financetracker/MainActivity.kt", "w") as f:
    f.writelines(out)
