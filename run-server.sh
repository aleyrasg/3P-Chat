#!/bin/bash

# Script automatizado para compilar y ejecutar el servidor de chat RMI

echo "Configurando Servidor de Chat RMI..."
echo "===================================="
echo ""

# Obtener la IP automáticamente
echo "[INFO] Tu dirección IP es:"
IP=$(ifconfig | grep "inet " | grep -v 127.0.0.1 | awk '{print $2}' | head -1)
echo "       $IP"
echo ""
echo "[IMPORTANTE] COMPARTE ESTA IP con los clientes"
echo ""

# Solicitar el puerto
read -p "Ingresa el puerto para RMI (recomendado: 1099): " PUERTO

# Usar puerto por defecto si no se ingresa nada
if [ -z "$PUERTO" ]; then
    PUERTO=1099
    echo "[INFO] Usando puerto por defecto: $PUERTO"
fi

echo ""
echo "[INFO] Compilando archivos del servidor..."

# Ir al directorio base
cd "$(dirname "$0")"

# Compilar las interfaces comunes primero
cd Common
javac ChatClientInterface.java
if [ $? -ne 0 ]; then
    echo "[ERROR] Error al compilar ChatClientInterface.java"
    exit 1
fi

javac ChatServerInterface.java
if [ $? -ne 0 ]; then
    echo "[ERROR] Error al compilar ChatServerInterface.java"
    exit 1
fi

# Copiar interfaces al directorio del servidor
cp *.class ../Server/
cd ../Server

# Compilar el servidor
javac ChatServer.java
if [ $? -ne 0 ]; then
    echo "[ERROR] Error al compilar ChatServer.java"
    exit 1
fi

echo "[INFO] Compilacion exitosa"
echo ""
echo "[INFO] Configurando java.rmi.server.hostname=$IP"
echo ""
echo "Iniciando servidor de chat..."
echo "============================="
echo ""

# Ejecutar el servidor pasando IP y puerto como parámetros
java -Djava.rmi.server.hostname=$IP ChatServer $IP $PUERTO
