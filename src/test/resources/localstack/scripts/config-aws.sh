#!/bin/bash

echo "===> [Config] Configurando AWS CLI para el entorno actual..."

# Configuración del HOST según variable de entorno
HOST="${LOCALSTACK_HOST:-localhost}"
AWS_ENDPOINT="http://${HOST}:4566"

# Configuración por defecto
AWS_REGION="us-east-1"
AWS_ACCESS_KEY_ID="test"
AWS_SECRET_ACCESS_KEY="test"

# Exportar variables de entorno para AWS CLI
export AWS_ACCESS_KEY_ID
export AWS_SECRET_ACCESS_KEY
export AWS_REGION

echo "===> [Config] Usando LocalStack en $AWS_ENDPOINT"

# Probar que AWS CLI funcione
echo "===> [Config] Listando buckets en LocalStack (si hay)..."
aws --endpoint-url="${AWS_ENDPOINT}" s3api list-buckets

echo "===> [Config] Configuración aplicada correctamente."
