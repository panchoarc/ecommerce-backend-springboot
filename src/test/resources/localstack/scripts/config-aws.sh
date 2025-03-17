#!/bin/bash

echo "===> [Config] Configurando AWS CLI para LocalStack..."

HOST=$(hostname)  # Obtiene el nombre del host


AWS_ENDPOINT="http://${HOST}:4566"
AWS_ACCESS_KEY_ID="test"
AWS_SECRET_ACCESS_KEY="test"

# Configura las credenciales de AWS
export AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}
export AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}

# Configura el endpoint de S3 en LocalStack directamente en los comandos, no en aws configure
echo "===> [Config] AWS CLI configurado correctamente."
echo "===> [Config] Endpoint: ${AWS_ENDPOINT}"

# Configuración del estilo de direccionamiento (path en lugar de virtual)
aws configure set default.s3.addressing_style path
