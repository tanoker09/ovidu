#!/bin/bash

set -e
set -x

PROFILE_NAME=${PROFILE_NAME:-"contest-streamer"} # Profile name from ~/.aws/credentials
TABLE_NAME="contest-streamer-data"

# (Re)create table

echo "Delete table $TABLE_NAME"

aws dynamodb delete-table \
  --profile $PROFILE_NAME \
  --table-name $TABLE_NAME \
  && sleep 5 || echo "Table not exists"

echo "Create table $TABLE_NAME"

aws dynamodb create-table \
  --profile $PROFILE_NAME \
  --table-name $TABLE_NAME \
  --attribute-definitions AttributeName=pk,AttributeType=S AttributeName=sk,AttributeType=S \
  --key-schema AttributeName=pk,KeyType=HASH AttributeName=sk,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST \
  && sleep 5

# Users

echo "Create users"

aws dynamodb put-item \
  --profile $PROFILE_NAME \
  --table-name $TABLE_NAME \
  --item '{
      "pk": {"S": "USER"},
      "sk": {"S": "achernenkov@nsalab.org"},
      "name": {"S": "Alexey Chernenkov"},
      "role": {"S": "admin"}
    }'