#!/bin/bash

echo "===> [Config] Configurando AWS CLI para LocalStack (Path-Style Addressing)..."

# Detectar si variable LOCALSTACK_HOST está seteada (útil en CI/CD o docker compose)
LOCALSTACK_HOSTNAME=${LOCALSTACK_HOST:-"localhost"}

# Cambia el endpoint a path-style (sin subdominio s3)
AWS_ENDPOINT="http://${LOCALSTACK_HOSTNAME}:4566"
AWS_ACCESS_KEY_ID="test"
AWS_SECRET_ACCESS_KEY="test"

# Mostrar configuración
echo "===> [Detect] LocalStack Host: ${LOCALSTACK_HOSTNAME}"
echo "===> [Detect] Endpoint URL: ${AWS_ENDPOINT}"

# Exportar credenciales como variables de entorno
export AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}
export AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}
export AWS_DEFAULT_REGION=us-east-1

# Configurar con awslocal
CLI_COMMAND="awslocal"

${CLI_COMMAND} configure set aws_access_key_id ${AWS_ACCESS_KEY_ID}
${CLI_COMMAND} configure set aws_secret_access_key ${AWS_SECRET_ACCESS_KEY}
${CLI_COMMAND} configure set region us-east-1
${CLI_COMMAND} configure set s3.endpoint_url "${AWS_ENDPOINT}"
${CLI_COMMAND} configure set default.s3.addressing_style path  # 👈 path-style aquí

echo "===> [Config] AWS CLI configurado en Path-Style correctamente."
