#!/bin/bash

echo "🎨 Compilando Cliente de Chat Moderno..."
echo "========================================"

# Compilar interfaces comunes
echo "📦 Compilando interfaces..."
cd Common
javac *.java
if [ $? -ne 0 ]; then
    echo "❌ Error al compilar interfaces"
    exit 1
fi

# Copiar clases al directorio del cliente
cp *.class ../Client/
cd ../Client

# Compilar cliente con GUI moderna
echo "🖥️  Compilando cliente GUI..."
javac ChatClientGUI.java
if [ $? -ne 0 ]; then
    echo "❌ Error al compilar cliente"
    exit 1
fi

echo "✅ Compilación exitosa!"
echo ""
echo "🚀 Iniciando Cliente de Chat Moderno..."
echo "======================================="

# Ejecutar cliente
java ChatClientGUI

echo ""
echo "👋 Cliente cerrado"