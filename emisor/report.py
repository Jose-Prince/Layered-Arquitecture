import os
import csv

class Report:
    """
    Representa un reporte del proceso de transmisión de un mensaje, incluyendo su codificación,
    transmisión y recepción, así como los errores detectados.
    """

    def __init__(self):
        """
        Inicializa un objeto Report con valores por defecto (vacíos o cero).
        Los atributos pueden ser configurados posteriormente usando los métodos `set_*`.
        """
        self.originalMessage = ""
        self.encodedMessage = ""
        self.transmittedMessage = ""
        self.selectedAlgorithm = ""
        self.isError = False
        self.errorRate = 0.0
        self.dataBitsCount = 0
        self.errorCount = 0

    def setOriginalMessage(self, message: str):
        """Asigna el mensaje original no codificado."""
        self.originalMessage = message

    def setEncodedMessage(self, message: str):
        """Asigna el mensaje codificado."""
        self.encodedMessage = message

    def setTransmittedMessage(self, message: str):
        """Asigna el mensaje transmitido (posiblemente con ruido)."""
        self.transmittedMessage = message

    def setSelectedAlgorithm(self, algorithm: str):
        """Asigna el algoritmo de codificación seleccionado."""
        self.selectedAlgorithm = algorithm

    def setIsError(self, isError: bool):
        """Indica si hubo error en la transmisión."""
        self.isError = isError

    def setErrorRate(self, errorRate: float):
        """Asigna la tasa de error de la transmisión (valor entre 0.0 y 1.0)."""
        self.errorRate = errorRate

    def setDataBitsCount(self, count: int):
        """Asigna la cantidad de bits de datos (excluyendo bits de control)."""
        self.dataBitsCount = count

    def updateErrorCount(self):
        """
        Calcula y actualiza la cantidad de bits erróneos entre el mensaje codificado
        y el mensaje transmitido.
        """
        if not self.encodedMessage or not self.transmittedMessage:
            self.errorCount = 0
            return

        errors = 0
        for i in range(min(len(self.encodedMessage), len(self.transmittedMessage))):
            if self.encodedMessage[i] != self.transmittedMessage[i]:
                errors += 1
        self.errorCount = errors

    def exportToCsv(self, filename="report.csv", directory="../data/"):
        """
        Exporta los datos del reporte a un archivo CSV. Si el archivo no existe, lo crea junto con la carpeta.
        Si el archivo está vacío, escribe también los encabezados.

        :param filename: Nombre del archivo CSV.
        :param directory: Carpeta donde se guardará el archivo.
        """
        os.makedirs(directory, exist_ok=True)
        path = os.path.join(directory, filename)

        # Datos a exportar
        data = {
            "originalMessage": self.originalMessage,
            "encodedMessage": self.encodedMessage,
            "transmittedMessage": self.transmittedMessage,
            "selectedAlgorithm": self.selectedAlgorithm,
            "isError": self.isError,
            "errorRate": self.errorRate,
            "dataBitsCount": self.dataBitsCount,
            "errorCount": self.errorCount
        }

        # Determinar si se debe escribir encabezado (archivo vacío o no existe)
        write_header = not os.path.exists(path) or os.path.getsize(path) == 0

        with open(path, mode="a", newline="", encoding="utf-8") as csvfile:
            writer = csv.DictWriter(csvfile, fieldnames=data.keys())
            if write_header:
                writer.writeheader()
            writer.writerow(data)