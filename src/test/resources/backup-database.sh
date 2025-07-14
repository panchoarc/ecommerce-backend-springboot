#!/bin/bash

CONTAINER_NAME=shared-postgres
USER=admin
DB=ecommerce_db
OUTPUT=data.sql

TABLES="-t endpoint -t role_endpoint -t role -t category"

docker exec -t $CONTAINER_NAME pg_dump -U $USER -d $DB --data-only --column-inserts $TABLES \
  | grep '^INSERT INTO' > $OUTPUT

echo "Backup completed in $(pwd)/$OUTPUT"
