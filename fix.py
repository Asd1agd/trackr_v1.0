with open('app/src/main/java/com/example/financetracker/MainActivity.kt', 'r') as f:
    lines = f.readlines()

package_line = ""
for line in lines:
    if line.startswith("package "):
        package_line = line
        break

lines = [line for line in lines if not line.startswith("package ")]
lines.insert(0, package_line + "\n")

with open('app/src/main/java/com/example/financetracker/MainActivity.kt', 'w') as f:
    f.writelines(lines)
