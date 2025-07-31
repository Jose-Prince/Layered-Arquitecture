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

## 📊 Generación del reporte

Para la fase de pruebas se generaron archivos CSV tanto del lado del emisor como del receptor, registrando información relevante como los mensajes originales, los bits codificados, el algoritmo utilizado, la tasa de error y los resultados de detección/corrección. Posteriormente, utilizando un entorno en Jupyter Notebook, se realizó la unificación de ambos CSV mediante el campo del mensaje transmitido con ruido, permitiendo así vincular cada envío con su respectiva recepción.

A partir de esta fusión se procesaron los datos para generar gráficas de análisis, lo cual permitió visualizar patrones, diferencias de comportamiento entre algoritmos y los efectos del ruido sobre la comunicación. Para reproducir este análisis, basta con ejecutar una prueba entre el emisor y el receptor al menos una vez, y luego instalar las dependencias necesarias desde el archivo `requirements.txt`. Esto asegura que se cuente con los registros mínimos necesarios para generar los reportes correctamente.

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

- Durante el desarrollo del laboratorio se pudo observar que el comportamiento del sistema bajo condiciones de ruido varía de forma significativa dependiendo del algoritmo utilizado, pero también de la integridad de la configuración que viaja junto con los datos. Un hallazgo clave fue que no basta con enviar el mensaje codificado correctamente: el receptor necesita interpretar correctamente la estructura, lo cual incluye no solo los bits de datos, sino también los bits de control, de paridad global, y especialmente los metadatos que indican qué algoritmo se utilizó. Cuando estos últimos son corrompidos por el ruido, el receptor puede aplicar un algoritmo diferente al que fue usado por el emisor, lo que lleva a fallas en la decodificación y a errores en cascada.

- Además, detectamos que en múltiples casos el ruido afectó tanto la integridad del mensaje como la interpretación de su longitud. Esto generó situaciones donde el receptor no pudo determinar correctamente cuántos bits eran realmente datos y cuántos correspondían a redundancia. Este desajuste provocó errores incluso cuando el mensaje parecía estructuralmente válido. También se identificaron errores donde el algoritmo detectado era incorrecto, y en consecuencia el receptor procesó el mensaje bajo suposiciones erróneas, lo que demuestra que el canal no confiable puede romper no solo el contenido sino también el protocolo.

- Los análisis permitieron comparar no solo tasas de error o éxito, sino también cómo responde cada algoritmo ante alteraciones estructurales del mensaje. Fue importante analizar el overhead agregado por cada uno, pero más aún cómo ese overhead contribuye (o no) a la resiliencia ante errores. En entornos donde se espera ruido elevado, se vuelve indispensable garantizar que la información crítica para la interpretación del mensaje (como tipo de algoritmo, estructura de los datos, y bits de control) tenga un nivel adicional de protección o redundancia, ya que su corrupción puede hacer fallar incluso al algoritmo más robusto.

- Este ejercicio nos permitió comprender que el éxito de un sistema de transmisión confiable no depende únicamente del algoritmo de detección o corrección utilizado, sino de una combinación de diseño cuidadoso, codificación de control resistente al ruido, y conciencia sobre los límites de lo que el receptor puede asumir sin garantías confiables sobre la metadata.

## Citas y Referencias
