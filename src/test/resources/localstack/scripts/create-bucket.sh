#!/bin/bash

echo "===> [S3] Creando bucket en LocalStack..."

# Configuración del HOST según variable de entorno
HOST="${LOCALSTACK_HOST:-localhost}"
AWS_ENDPOINT="http://${HOST}:4566"

BUCKET_NAME="ecommerce-buyit-bucket"

echo "===> [S3] AWS_ENDPOINT actual es: ${AWS_ENDPOINT}"

# Verificar que awslocal esté disponible
if ! command -v awslocal &> /dev/null; then
    echo "===> [S3] ERROR: awslocal no está instalado. Instálalo con: pip install localstack-client"
    exit 1
fi

# Crear bucket S3
aws --endpoint-url="${AWS_ENDPOINT}" s3api create-bucket --bucket ${BUCKET_NAME}

# Configurar CORS para el bucket
echo "===> [S3] Configurando CORS para el bucket..."
aws --endpoint-url="${AWS_ENDPOINT}" s3api put-bucket-cors \
  --bucket ${BUCKET_NAME} \
  --cors-configuration file:///etc/localstack/init/ready.d/cors.json

echo "===> [S3] CORS configurado correctamente."

# Listar buckets existentes
echo "===> [S3] Buckets existentes:"
aws --endpoint-url="${AWS_ENDPOINT}" s3api list-buckets

# Mostrar URL informativa
echo "===> [S3] Bucket disponible en:"
echo "${AWS_ENDPOINT}"
