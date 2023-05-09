#!/bin/bash
echo executing SQL statements...
psql -d shotmaniacs -f sql/create-tables.sql
psql -d shotmaniacs -f sql/insert-table-data.sql
psql -d shotmaniacs -f sql/insert-users.sql