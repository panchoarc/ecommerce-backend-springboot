#!/bin/bash

echo "===> [Config] Configurando AWS CLI para LocalStack (Path-Style Addressing)..."

# Detectar si corro en contenedor o en host
if grep -qE "(docker|containerd)" /proc/1/cgroup; then
  LOCALSTACK_HOSTNAME=${LOCALSTACK_HOST:-"localstack"}
else
  LOCALSTACK_HOSTNAME=${LOCALSTACK_HOST:-"localhost"}
fi

AWS_ENDPOINT="http://${LOCALSTACK_HOSTNAME}:4566"
AWS_ACCESS_KEY_ID="test"
AWS_SECRET_ACCESS_KEY="test"
PROFILE_NAME="localstack"

echo "===> [Detect] LocalStack Host: ${LOCALSTACK_HOSTNAME}"
echo "===> [Detect] Endpoint URL: ${AWS_ENDPOINT}"

# Esperar a que LocalStack esté listo
until curl -s "${AWS_ENDPOINT}" > /dev/null; do
  echo "Esperando a que LocalStack esté disponible en ${AWS_ENDPOINT}..."
  sleep 2
done

# Exportar credenciales como variables de entorno
export AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}
export AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}
export AWS_DEFAULT_REGION=us-east-1

# Configurar AWS CLI (profile aislado)
CLI_COMMAND="awslocal"

${CLI_COMMAND} configure set aws_access_key_id ${AWS_ACCESS_KEY_ID} --profile ${PROFILE_NAME}
${CLI_COMMAND} configure set aws_secret_access_key ${AWS_SECRET_ACCESS_KEY} --profile ${PROFILE_NAME}
${CLI_COMMAND} configure set region us-east-1 --profile ${PROFILE_NAME}
${CLI_COMMAND} configure set default.s3.addressing_style path --profile ${PROFILE_NAME}

# Solo si vas a usar aws-cli (no awslocal) tiene sentido:
# ${CLI_COMMAND} configure set s3.endpoint_url "${AWS_ENDPOINT}" --profile ${PROFILE_NAME}

echo "===> [Config] AWS CLI configurado en Path-Style correctamente (Profile: ${PROFILE_NAME})."
