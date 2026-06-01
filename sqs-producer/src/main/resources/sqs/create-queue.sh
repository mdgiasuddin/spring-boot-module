#!/bin/bash

set -e

ENDPOINT="http://localhost:4766"
REGION="us-east-1"
QUEUE_NAME="my-queue"

echo "Creating SQS queue: $QUEUE_NAME"

aws --endpoint-url=$ENDPOINT \
    --region $REGION \
    sqs create-queue \
    --queue-name my-queue

echo "Queue created successfully!"

echo "Fetching queue URL..."
aws --endpoint-url=$ENDPOINT \
    --region $REGION \
    sqs get-queue-url \
    --queue-name $QUEUE_NAME

echo "Done."