cat << 'INNER' >> app/src/main/java/com/example/financetracker/MainActivity.kt

private fun documentName(contentResolver: android.content.ContentResolver, uri: android.net.Uri): String {
    var name = "imported_file"
    try {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = cursor.getString(nameIndex)
                }
            }
        }
    } catch (e: Exception) {
        // ignore
    }
    return name
}
INNER
