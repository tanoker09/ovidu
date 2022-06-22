#!/bin/bash

set -e
set -x

ENDPOINT_URL=${ENDPOINT_URL:-"http://localhost:8000"} # Local DynamoDB URL
TABLE_NAME="contest-streamer-data"

TS="2021-02-08T08:01:20.606Z"

START_AT="2021-02-20T08:00:00.000Z"
FINISH_AT="2021-04-10T00:00:00.000Z"

# (Re)create table

echo "Delete table $TABLE_NAME"

aws dynamodb delete-table \
  --endpoint-url $ENDPOINT_URL \
  --table-name $TABLE_NAME \
  || echo "Table not exists"

echo "Create table $TABLE_NAME"

aws dynamodb create-table \
  --endpoint-url $ENDPOINT_URL \
  --table-name $TABLE_NAME \
  --attribute-definitions AttributeName=pk,AttributeType=S AttributeName=sk,AttributeType=S \
  --key-schema AttributeName=pk,KeyType=HASH AttributeName=sk,KeyType=RANGE \
  --provisioned-throughput ReadCapacityUnits=100,WriteCapacityUnits=100

# Users

echo "Create users"

aws dynamodb put-item \
  --endpoint-url $ENDPOINT_URL \
  --table-name $TABLE_NAME \
  --item '{
      "pk": {"S": "USER"},
      "sk": {"S": "ivan@mail.ru"},
      "name": {"S": "Ivan Ivanov"},
      "role": {"S": "contestant"},
      "createdAt": {"S": "'$TS'"},
      "updatedAt": {"S": "'$TS'"}
    }'

aws dynamodb put-item \
  --endpoint-url $ENDPOINT_URL \
  --table-name $TABLE_NAME \
  --item '{
      "pk": {"S": "USER"},
      "sk": {"S": "petr@mail.ru"},
      "name": {"S": "Petr Petrov"},
      "role": {"S": "watcher"},
      "createdAt": {"S": "'$TS'"},
      "updatedAt": {"S": "'$TS'"}
    }'

aws dynamodb put-item \
  --endpoint-url $ENDPOINT_URL \
  --table-name $TABLE_NAME \
  --item '{
      "pk": {"S": "USER"},
      "sk": {"S": "egor@mail.ru"},
      "name": {"S": "Egor Egorov"},
      "role": {"S": "admin"},
      "createdAt": {"S": "'$TS'"},
      "updatedAt": {"S": "'$TS'"}
    }'

# Contests

echo "Create contests"

for contestId in $(seq 1 3); do
  aws dynamodb put-item \
    --endpoint-url $ENDPOINT_URL \
    --table-name $TABLE_NAME \
    --item '{
        "pk": {"S": "CONTEST"},
        "sk": {"S": "'$contestId'"},
        "name": {"S": "Contest #'$contestId'"},
        "status": {"S": "active"},
        "startAt": {"S": "'$START_AT'"},
        "finishAt": {"S": "'$FINISH_AT'"},
        "awsInstCreated": {"BOOL": true},
        "awsInstType": {"S": "c5.xlarge"},
        "awsInstId": {"S": "i-06eada1375b66744'$contestId'"},
        "awsIpAllocated": {"BOOL": true},
        "awsIpAllocId": {"S": "eipalloc-64d5890'$contestId'"},
        "awsIpAllocAddr": {"S": "3.127.150.14'$contestId'"},
        "awsIpAssociated": {"BOOL": true},
        "awsIpAssocId": {"S": "eipassoc-2bebb74'$contestId'"},
        "awsDnsRecorded": {"BOOL": true},
        "awsDnsDomain": {"S": "contest-streamer-openvidu-'$contestId'.dev.nsalab.org"},
        "awsLastError": {"S": "Unknown error happened!"},
        "openViduSecret": {"S": "aRfs5Jw6v"},
        "createdAt": {"S": "'$TS'"},
        "updatedAt": {"S": "'$TS'"}
      }'
done

# Contests/users

echo "Create contests/users relationships"

for userId in ivan@mail.ru petr@mail.ru; do
  for contestId in $(seq 1 3); do
    aws dynamodb put-item \
      --endpoint-url $ENDPOINT_URL \
      --table-name $TABLE_NAME \
      --item '{
          "pk": {"S": "CONTEST_USER#'$contestId'"},
          "sk": {"S": "'$userId'"},
          "createdAt": {"S": "'$TS'"},
          "updatedAt": {"S": "'$TS'"}
        }'

    aws dynamodb put-item \
      --endpoint-url $ENDPOINT_URL \
      --table-name $TABLE_NAME \
      --item '{
          "pk": {"S": "USER_CONTEST#'$userId'"},
          "sk": {"S": "active#'$contestId'"},
          "contestName": {"S": "Contest #'$contestId'"},
          "contestStartAt": {"S": "'$START_AT'"},
          "contestFinishAt": {"S": "'$FINISH_AT'"},
          "createdAt": {"S": "'$TS'"},
          "updatedAt": {"S": "'$TS'"}
        }'
  done
done
