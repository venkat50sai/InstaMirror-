#!/usr/bin/env bash
set -e

echo "Starting backend services in single-container mode"

# Helper to find a jar by name pattern
find_jar() {
  name="$1"
  jar=$(ls /app | grep -i "${name}" | head -n 1 || true)
  if [ -z "$jar" ]; then
    # try fallback pattern
    jar=$(ls /app | grep -E "${name}.*SNAPSHOT.*jar" | head -n1 || true)
  fi
  echo "$jar"
}

# Environment variables (with Railway defaults)
export MYSQL_HOST=${MYSQL_HOST:-mysql}
export MYSQL_PORT=${MYSQL_PORT:-3306}
export MYSQL_DB=${MYSQL_DB:-railway}
export MYSQL_USER=${MYSQL_USER:-root}
export MYSQL_PASS=${MYSQL_PASS:-}

export EUREKA_URL=${EUREKA_URL:-http://localhost:8761/eureka}

# Debug: Show environment
echo "=== Configuration ==="
echo "MYSQL_HOST=$MYSQL_HOST"
echo "MYSQL_PORT=$MYSQL_PORT"
echo "MYSQL_DB=$MYSQL_DB"
echo "MYSQL_USER=$MYSQL_USER"
echo "EUREKA_URL=$EUREKA_URL"
echo "===================="

# Start Eureka
EUREKA_JAR=$(find_jar "eureka")
if [ -n "$EUREKA_JAR" ]; then
  echo "Starting Eureka: $EUREKA_JAR"
  java -Deureka.client.register-with-eureka=false -Dserver.port=8761 -jar /app/$EUREKA_JAR > /app/eureka.log 2>&1 &
else
  echo "Eureka jar not found, skipping Eureka start"
fi

# wait for Eureka to be available (if started)
if [ -n "$EUREKA_JAR" ]; then
  echo "Waiting for Eureka to start..."
  for i in {1..30}; do
    if curl -s $EUREKA_URL/ > /dev/null 2>&1; then
      echo "Eureka reachable"
      break
    fi
    sleep 2
    echo "retry $i/30"
  done
fi

# Function to start a service jar with given port and name
start_service() {
  svc_name=$1
  port=$2
  jar=$(find_jar "$svc_name")
  if [ -z "$jar" ]; then
    echo "Jar for $svc_name not found, skipping"
    return
  fi
  echo "Starting $svc_name on port $port (jar: $jar)"
  java -Dserver.port=$port \
    -Deureka.client.service-url.defaultZone=$EUREKA_URL \
    -Dspring.datasource.url=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DB} \
    -Dspring.datasource.username=${MYSQL_USER} \
    -Dspring.datasource.password=${MYSQL_PASS} \
    -jar /app/$jar > /app/${svc_name}.log 2>&1 &
}

# Start gateway and microservices
start_service "gate" 8888
sleep 1
start_service "user" 8081
start_service "post" 8082
start_service "comment" 8083
start_service "like" 8084
start_service "friend" 8085
start_service "product" 8086
start_service "order" 8087
start_service "cart" 8088

echo "All services started. Waiting for initialization..."
sleep 10

# Show all service logs
echo "=== SERVICE INITIALIZATION STATUS ==="
for log in /app/*.log; do
  svc=$(basename "$log" .log)
  echo "--- $svc ---"
  tail -20 "$log" 2>/dev/null || echo "No logs yet"
done
echo "====================================="

# Keep container alive and tail all logs
tail -f /app/*.log 2>/dev/null || true
