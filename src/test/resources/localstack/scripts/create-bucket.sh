#!/bin/bash

echo "===> [S3] Configurando LocalStack (Bucket y CORS)..."

BUCKET_NAME="ecommerce-buyit-bucket"

# Detectar el host de LocalStack (localstack o localhost)
LOCALSTACK_HOST=${LOCALSTACK_HOST:-"localhost"}

AWS_ENDPOINT="http://${LOCALSTACK_HOST}:4566"

echo "===> [S3] Usando endpoint: $AWS_ENDPOINT"

# Paso 1: Esperar a que LocalStack esté listo
echo "===> [S3] Esperando a que LocalStack (S3) esté disponible en $AWS_ENDPOINT..."

echo "===> [S3] LocalStack (S3) está listo!"

# Paso 2: Crear bucket (ignorar error si ya existe)
awslocal --endpoint-url="$AWS_ENDPOINT" s3api create-bucket --bucket "$BUCKET_NAME" || true

# Paso 3: Configurar CORS
echo "===> [S3] Configurando CORS para el bucket..."
awslocal --endpoint-url="$AWS_ENDPOINT" s3api put-bucket-cors \
  --bucket "$BUCKET_NAME" \
  --cors-configuration file:///etc/localstack/init/ready.d/cors.json

# Paso 4: Mostrar buckets existentes
echo "===> [S3] Buckets existentes:"
awslocal --endpoint-url="$AWS_ENDPOINT" s3api list-buckets

echo "===> [S3] Bucket disponible en: $AWS_ENDPOINT/$BUCKET_NAME"
