#!/bin/bash

echo "===> [S3] Creando bucket en LocalStack..."

# Configuración
BUCKET_NAME="ecommerce-buyit-bucket"
AWS_ENDPOINT="http://localhost:4566"

awslocal --endpoint-url=${AWS_ENDPOINT} s3api create-bucket --bucket ${BUCKET_NAME}

echo "===> [S3] Buckets existentes:"
awslocal --endpoint-url=${AWS_ENDPOINT} s3api list-buckets

echo "===> [S3] Bucket disponible en:"
echo "http://localhost:4566/${BUCKET_NAME}"
