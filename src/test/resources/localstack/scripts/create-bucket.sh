#!/bin/bash
set -x

echo "===> [S3] Creando bucket en LocalStack..."

HOST="${LOCALSTACK_HOST:-localhost}"
AWS_ENDPOINT="http://${HOST}:4566"
BUCKET_NAME="ecommerce-buyit-bucket"

export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=us-east-1

echo "===> [S3] AWS_ENDPOINT actual es: ${AWS_ENDPOINT}"

# Opcional, darle tiempo a que S3 esté listo
sleep 5

# Crear bucket S3 con región explícita
aws --endpoint-url="${AWS_ENDPOINT}" --region us-east-1 s3api create-bucket --bucket ${BUCKET_NAME}

echo "===> [S3] Configurando CORS para el bucket..."
aws --endpoint-url="${AWS_ENDPOINT}" --region us-east-1 s3api put-bucket-cors \
  --bucket ${BUCKET_NAME} \
  --cors-configuration file:///etc/localstack/init/ready.d/cors.json

echo "===> [S3] CORS configurado correctamente."

echo "===> [S3] Buckets existentes:"
aws --endpoint-url="${AWS_ENDPOINT}" --region us-east-1 s3api list-buckets

echo "===> [S3] Bucket disponible en:"
echo "${AWS_ENDPOINT}/${BUCKET_NAME}"
