#!/bin/bash

set -e

ENDPOINT="http://localhost:4866"
REGION="us-east-1"
BUCKET_NAME="employee-csv"

echo "Creating S3 bucket: $BUCKET_NAME"

aws --endpoint-url=$ENDPOINT \
    --region $REGION \
    s3 mb s3://$BUCKET_NAME || echo "Bucket $BUCKET_NAME already exists or failed to create"

echo "Bucket created successfully!"

echo "Listing buckets..."
aws --endpoint-url=$ENDPOINT \
    --region $REGION \
    s3 ls

echo "Verification: Checking if $BUCKET_NAME exists..."
if aws --endpoint-url=$ENDPOINT --region $REGION s3 ls "s3://$BUCKET_NAME" > /dev/null 2>&1; then
    echo "Bucket $BUCKET_NAME exists."
else
    echo "ERROR: Bucket $BUCKET_NAME DOES NOT EXIST!"
    exit 1
fi

echo "Done."
