#!/usr/bin/env bash
set -euo pipefail

# wait-for-db.sh
# Wait for the database host:port to accept TCP connections before starting the Spring Boot app.
# Supports parsing DB host/port from a JDBC URL (e.g., jdbc:mysql://host:3306/dbname)

DB_URL=${DB_URL-}
DB_HOST=${DB_HOST-}
DB_PORT=${DB_PORT-}
DB_WAIT_TIMEOUT=${DB_WAIT_TIMEOUT-60}
DB_WAIT_INTERVAL=${DB_WAIT_INTERVAL-2}
DB_WAIT_DRY_RUN=${DB_WAIT_DRY_RUN-0}

# Trim potential CR/LF and surrounding whitespace (helps when env vars are set from Windows CMD)
DB_WAIT_DRY_RUN=$(printf '%s' "$DB_WAIT_DRY_RUN" | tr -d '\r' | tr -d '[:space:]')
DB_HOST=$(printf '%s' "$DB_HOST" | tr -d '\r' | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')
DB_URL=$(printf '%s' "$DB_URL" | tr -d '\r' | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')
DB_PORT=$(printf '%s' "$DB_PORT" | tr -d '\r' | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')

if [ -z "${DB_HOST}" ]; then
  if [[ -n "${DB_URL}" && "${DB_URL}" == jdbc:mysql://* ]]; then
    # Extract host[:port] between jdbc:mysql:// and first / or ?
    HOSTPORT=$(echo "${DB_URL}" | sed -E 's#jdbc:mysql://([^/\?]+).*#\1#')
    DB_HOST=$(echo "${HOSTPORT}" | cut -d: -f1)
    DB_PORT=$(echo "${HOSTPORT}" | cut -s -d: -f2)
  fi
fi

DB_PORT=${DB_PORT:-3306}

echo "Resolved DB_HOST=${DB_HOST:-}<unset> DB_PORT=${DB_PORT} DB_URL=${DB_URL:-}<unset>"

if [ "${DB_WAIT_DRY_RUN}" = "1" ]; then
  echo "Dry-run mode: exiting without starting application."
  exit 0
fi

if [ -z "${DB_HOST}" ]; then
  echo "DB_HOST is not set and DB_URL could not be parsed; starting application immediately."
  exec java $JAVA_OPTS -Dserver.port=${PORT:-8081} -Dfile.storage.upload-dir=${FILE_STORAGE_UPLOAD_DIR:-/data/ilas/uploads} -jar /app/app.jar
fi

elapsed=0
while true; do
  echo "Checking TCP ${DB_HOST}:${DB_PORT} (elapsed ${elapsed}s)..."
  # Use bash built-in /dev/tcp for a quick TCP check
  if (echo > /dev/tcp/${DB_HOST}/${DB_PORT}) >/dev/null 2>&1; then
    echo "Database ${DB_HOST}:${DB_PORT} is available"
    break
  fi

  if [ ${elapsed} -ge ${DB_WAIT_TIMEOUT} ]; then
    echo "Timed out after ${DB_WAIT_TIMEOUT}s waiting for ${DB_HOST}:${DB_PORT}" >&2
    exit 1
  fi

  sleep ${DB_WAIT_INTERVAL}
  elapsed=$((elapsed + DB_WAIT_INTERVAL))
done

exec java $JAVA_OPTS -Dserver.port=${PORT:-8081} -Dfile.storage.upload-dir=${FILE_STORAGE_UPLOAD_DIR:-/data/ilas/uploads} -jar /app/app.jar
