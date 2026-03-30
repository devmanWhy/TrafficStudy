#!/bin/bash

echo "Waiting for Kafka Connect to start..."
sleep 5

echo "Registering MySQL Debezium Connector..."
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" \
  http://localhost:18083/connectors/ \
  -d @register-mysql-connector.json

echo ""
echo "Connector registration completed!"
echo ""
echo "Check connector status:"
echo "curl http://localhost:18083/connectors/traffic-study-mysql-connector/status"