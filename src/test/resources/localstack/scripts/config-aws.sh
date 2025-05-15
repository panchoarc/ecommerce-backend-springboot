#!/bin/bash

echo "===> [Config] Configurando AWS CLI para el entorno actual..."

# Configuración del HOST según variable de entorno
HOST="${LOCALSTACK_HOST:-localhost}"
AWS_ENDPOINT="http://${HOST}:4566"

# Configuración por defecto
AWS_REGION="us-east-1"
AWS_ACCESS_KEY_ID="test"
AWS_SECRET_ACCESS_KEY="test"

# Exportar para AWS CLI
export AWS_ACCESS_KEY_ID
export AWS_SECRET_ACCESS_KEY
export AWS_REGION

echo "===> [Config] Usando LocalStack en $AWS_ENDPOINT"

# Verificar si awslocal está disponible
if command -v awslocal &> /dev/null; then
    echo "===> [Config] awslocal detectado. Configurando..."
else
    echo "===> [Config] ERROR: awslocal no está instalado. Instálalo con: pip install localstack-client"
    exit 1
fi

# Configurar CLI (aunque awslocal ya respeta env vars, lo dejamos explícito)
awslocal configure set aws_access_key_id ${AWS_ACCESS_KEY_ID}
awslocal configure set aws_secret_access_key ${AWS_SECRET_ACCESS_KEY}
awslocal configure set region ${AWS_REGION}
awslocal configure set s3.endpoint_url ${AWS_ENDPOINT}
awslocal configure set default.s3.addressing_style path

# Mostrar configuración
echo "===> [Config] Configuración actual:"
awslocal configure list
