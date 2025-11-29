#!/usr/bin/env bash
set -euo pipefail

# This script runs inside the Postgres container at first initialization.
# It creates tables and imports CSVs by piping them into psql with COPY ... FROM STDIN.
# Files are expected at /docker-entrypoint-initdb.d/<filename> (mounted from host ./db).

echo "init.sh: creating tables..."

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<'SQL'
CREATE TABLE IF NOT EXISTS catalog_100 (
  id SERIAL PRIMARY KEY,
  title TEXT NOT NULL,
  author TEXT,
  year INT,
  search_count INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS catalog_1000 (
  id SERIAL PRIMARY KEY,
  title TEXT NOT NULL,
  author TEXT,
  year INT,
  search_count INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS catalog_10000 (
  id SERIAL PRIMARY KEY,
  title TEXT NOT NULL,
  author TEXT,
  year INT,
  search_count INT DEFAULT 0
);
SQL

echo "init.sh: tables created. Importing CSVs..."

# Helper function: import CSV at path into table using COPY FROM STDIN
_import_csv() {
  local csv_path="$1"
  local table_name="$2"
  if [ -f "$csv_path" ]; then
    echo "Importing $csv_path -> $table_name"
    # Use psql with COPY FROM STDIN; piping avoids server-side file permission issues.
    # We explicitly set CSV HEADER so the header row is skipped.
    cat "$csv_path" | psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" -c "COPY ${table_name} (title,author,year,search_count) FROM STDIN WITH CSV HEADER;"
    echo "Imported $csv_path -> $table_name (rows: $(psql -q -t -A -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "SELECT COUNT(*) FROM ${table_name};"))"
  else
    echo "Warning: file not found: $csv_path"
  fi
}

_import_csv "/docker-entrypoint-initdb.d/catalog_100.csv" "catalog_100"
_import_csv "/docker-entrypoint-initdb.d/catalog_1000.csv" "catalog_1000"
_import_csv "/docker-entrypoint-initdb.d/catalog_10000.csv" "catalog_10000"

echo "init.sh: done."
