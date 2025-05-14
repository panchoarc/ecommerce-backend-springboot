#!/bin/bash

# Detectar entorno y configurar variables apropiadamente
echo "===> [Config] Configurando AWS CLI para el entorno actual..."

# Función para detectar si estamos en GitHub Actions
is_github_actions() {
    [ -n "$GITHUB_ACTIONS" ]
}

# Asignar HOST según entorno
if is_github_actions; then
    HOST=$(hostname)
else
    HOST="localhost"
fi

# Valores por defecto (entorno local o GitHub Actions)
AWS_ENDPOINT_DEFAULT="http://${HOST}:4566"
AWS_REGION_DEFAULT="us-east-1"
AWS_ACCESS_KEY_ID_DEFAULT="test"
AWS_SECRET_ACCESS_KEY_DEFAULT="test"
USE_LOCALSTACK_DEFAULT="true"

# Leer variables de entorno si están definidas, sino usar valores por defecto
AWS_ENDPOINT="${AWS_ENDPOINT:-$AWS_ENDPOINT_DEFAULT}"
AWS_REGION="${AWS_REGION:-$AWS_REGION_DEFAULT}"
AWS_ACCESS_KEY_ID="${AWS_ACCESS_KEY_ID:-$AWS_ACCESS_KEY_ID_DEFAULT}"
AWS_SECRET_ACCESS_KEY="${AWS_SECRET_ACCESS_KEY:-$AWS_SECRET_ACCESS_KEY_DEFAULT}"
USE_LOCALSTACK="${USE_LOCALSTACK:-$USE_LOCALSTACK_DEFAULT}"

# Exportar las variables para que sean accesibles por los comandos AWS CLI
export AWS_ACCESS_KEY_ID
export AWS_SECRET_ACCESS_KEY
export AWS_REGION

# Configurar AWS CLI basado en el entorno
if [ "$USE_LOCALSTACK" = "true" ]; then
    echo "===> [Config] Usando LocalStack en $AWS_ENDPOINT"

    # Verificar si awslocal está instalado
    if command -v awslocal &> /dev/null; then
        # Configurar awslocal para LocalStack
        awslocal configure set aws_access_key_id ${AWS_ACCESS_KEY_ID}
        awslocal configure set aws_secret_access_key ${AWS_SECRET_ACCESS_KEY}
        awslocal configure set region ${AWS_REGION}
        awslocal configure set s3.endpoint_url ${AWS_ENDPOINT}
        awslocal configure set default.s3.addressing_style path

        # Crear función para usar awslocal en lugar de aws
        aws() {
            awslocal "$@"
        }
    else
        echo "===> [Config] AVISO: 'awslocal' no está instalado, configurando aws CLI estándar para LocalStack"

        # Configurar AWS CLI estándar para apuntar a LocalStack
        aws configure set aws_access_key_id ${AWS_ACCESS_KEY_ID}
        aws configure set aws_secret_access_key ${AWS_SECRET_ACCESS_KEY}
        aws configure set region ${AWS_REGION}
        aws configure set endpoint_url ${AWS_ENDPOINT}
        aws configure set s3.endpoint_url ${AWS_ENDPOINT}
        aws configure set default.s3.addressing_style virtual
    fi
else
    echo "===> [Config] Usando AWS real en región $AWS_REGION"

    # Configurar AWS CLI para el servicio AWS real
    aws configure set aws_access_key_id ${AWS_ACCESS_KEY_ID}
    aws configure set aws_secret_access_key ${AWS_SECRET_ACCESS_KEY}
    aws configure set region ${AWS_REGION}
    aws configure set default.s3.addressing_style virtual

    # Eliminar endpoint personalizado si existe
    aws configure unset s3.endpoint_url
    aws configure unset endpoint_url
fi

# Mostrar información de configuración
echo "===> [Config] AWS CLI configurado correctamente."
echo "===> [Config] Entorno: $(if is_github_actions; then echo "GitHub Actions"; else echo "Local"; fi)"
echo "===> [Config] Usando LocalStack: $USE_LOCALSTACK"
echo "===> [Config] Región: $AWS_REGION"
if [ "$USE_LOCALSTACK" = "true" ]; then
    echo "===> [Config] Endpoint: $AWS_ENDPOINT"
fi

# Verificar la configuración (opcional)
echo "===> [Config] Verificando configuración..."
if [ "$USE_LOCALSTACK" = "true" ] && command -v awslocal &> /dev/null; then
    awslocal configure list
else
    aws configure list
fi
