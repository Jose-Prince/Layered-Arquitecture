# Laboratorio 2 Parte 2: Esquemas de detección y corrección de errores🏗️

## Integrantes

- José Prince #22087
- Josue Say #22801

## Descripción de la práctica

En esta práctica de laboratorio se debe de desarrollar una arquitectura de capas, con las siguientes capas: Aplicación, Presentación, Enlace, Ruido (unicamente aplicable al emisor) y Transmisión. La implementación del emisor y receptor se realiza con diferentes lenguajes de programación. El envío de información del emisor al receptor se hace mediante sockets. Por cada servicio (emisor y receptor) se aplican algoritmos de detección y corrección de errores, estos permitiran codificar y decoficar el mensaje que se enviá.

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

## 🔧 Hamming Configuración

```bash
[1 bit algoritmo][N bits indicando m][bits codificados con Hamming][paridad global opcional]
```

- El bit del algoritmo depende del valor que se coloca en el archivo `protocol.yaml` actualmente se coloca 0 para Hamming.
- Los bits apartados para representar la cantidad de bits de data son 5 y estan definidos en `protocol.yaml`.
- Los bits codificados con Hamming es el proceso hecho por el algoritmo (este recibe una cadena de bits que se pasa a binario de 8 bits actualmente el cual se define en `protocol.yaml`).
- El bit de paridad global utilizado para detectar más de 1 error, este bits se coloca siempre y cuando la propiedadad `extended` esta como `true` en `protocol.yaml`.

## Generación de reporte

## 🔄 Avances

Avances de la primera entrega creación de capa de aplicación, presentación, algoritmo y ruido.

![Screenshot](./images/screenshot.png)

## Resultados

A continuación se muestran las diferentes pruebas que se realizaron. Para la realización de las pruebas la tasa de error nunca fue igual a 1, esto para evitar tribialidades ya que se sabe que con una tasa de error del 0% el mensaje siempre se envia correctamente.


## Discusión

![Screenshot](./images/graficas/gp1.png)
![Screenshot](./images/graficas/gp2.png)
![Screenshot](./images/graficas/gp3.png)
![Screenshot](./images/graficas/gp4.png)
![Screenshot](./images/graficas/gp5.png)
![Screenshot](./images/graficas/gp6.png)
![Screenshot](./images/graficas/gp7.png)
![Screenshot](./images/graficas/gp8.png)

## Conclusiones

## Citas y Referencias
