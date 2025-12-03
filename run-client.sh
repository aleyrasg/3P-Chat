#!/bin/bash

# Script automatizado para compilar y ejecutar el cliente de chat RMI con GUI

echo "Compilando Cliente de Chat RMI..."
echo "================================="

# Ir al directorio base
cd "$(dirname "$0")"

echo "[INFO] Compilando interfaces..."

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

# Copiar interfaces al directorio del cliente
cp *.class ../Client/
cd ../Client

# Compilar el cliente
javac ChatClientGUI.java
if [ $? -ne 0 ]; then
    echo "[ERROR] Error al compilar ChatClientGUI.java"
    exit 1
fi

echo "[INFO] Compilacion exitosa"
echo ""
echo "Iniciando Cliente de Chat RMI..."
echo "==============================="

# Ejecutar el cliente
java ChatClientGUI

if [ $? -ne 0 ]; then
    echo ""
    echo "[ERROR] Error al ejecutar el cliente"
    exit 1
fi
