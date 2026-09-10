with open('app/src/main/java/com/example/financetracker/data/FinanceDatabase.kt', 'r') as f:
    content = f.read()

content = content.replace(
    "INSERT INTO categories (name, keywords) VALUES ('Entertainment', 'netflix,spotify,amazon prime,movie,cinema')",
    "INSERT INTO categories (name, keywords, isEssential) VALUES ('Entertainment', 'netflix,spotify,amazon prime,movie,cinema', 0)"
)
content = content.replace(
    "INSERT INTO categories (name, keywords) VALUES ('Trip', 'hotel,flight,airbnb,make my trip,goibibo')",
    "INSERT INTO categories (name, keywords, isEssential) VALUES ('Trip', 'hotel,flight,airbnb,make my trip,goibibo', 0)"
)
content = content.replace(
    "INSERT INTO categories (name, keywords) VALUES ('Bike', 'petrol,service,repair,bike,fuel')",
    "INSERT INTO categories (name, keywords, isEssential) VALUES ('Bike', 'petrol,service,repair,bike,fuel', 0)"
)
content = content.replace(
    "INSERT INTO categories (name, keywords) VALUES ('Online Shopping', 'amazon,flipkart,myntra,shopping,shoes,clothes')",
    "INSERT INTO categories (name, keywords, isEssential) VALUES ('Online Shopping', 'amazon,flipkart,myntra,shopping,shoes,clothes', 0)"
)

# And fix MIGRATION_1_2 just in case
content = content.replace(
    "val MIGRATION_1_2 = object : Migration(1, 2) {\n    override fun migrate(db: SupportSQLiteDatabase) {\n        db.execSQL(\"INSERT INTO categories (name, keywords) VALUES ('Trip', 'hotel,flight,airbnb,make my trip,goibibo')\")\n        db.execSQL(\"INSERT INTO categories (name, keywords) VALUES ('Bike', 'petrol,service,repair,bike,fuel')\")\n        db.execSQL(\"INSERT INTO categories (name, keywords) VALUES ('Online Shopping', 'amazon,flipkart,myntra,shopping,shoes,clothes')\")\n    }\n}",
    "val MIGRATION_1_2 = object : Migration(1, 2) {\n    override fun migrate(db: SupportSQLiteDatabase) {\n        // These ran on v2, which didn't have isEssential.\n        db.execSQL(\"INSERT INTO categories (name, keywords) VALUES ('Trip', 'hotel,flight,airbnb,make my trip,goibibo')\")\n        db.execSQL(\"INSERT INTO categories (name, keywords) VALUES ('Bike', 'petrol,service,repair,bike,fuel')\")\n        db.execSQL(\"INSERT INTO categories (name, keywords) VALUES ('Online Shopping', 'amazon,flipkart,myntra,shopping,shoes,clothes')\")\n    }\n}"
)


with open('app/src/main/java/com/example/financetracker/data/FinanceDatabase.kt', 'w') as f:
    f.write(content)
