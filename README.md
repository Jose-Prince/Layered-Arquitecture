# Layered Arquitecture 🏗️

## 🌐 Entorno

- **Python:** 3.10.12  
- **Maven:** 3.6.3  
- **Java:** OpenJDK 21.0.8 (Ubuntu)  
- **SO:** Linux (WSL2), kernel 6.6.87.2-microsoft-standard-wsl2, amd64  

## 🚀 Ejecutar el programa

### Consola del **Emisor**

```bash
cd emisor
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
python main.py
```

### Consola del **Receptor**

```bash
cd receptor
mvn clean compile
mvn exec:java
```

## 🔄 Avances

Avances de la primera entrega creación de capa de aplicación, presentación, algoritmo y ruido.

![Screenshot](./images/screenshot.png)
