from application import getMsg
from presentation import stringToBin
from data_link import applyAlgorithm 
from noise import make_noise
from transmission import sendMsg
from report import Report
import yaml

def loadProtocol(path="../protocol.yaml"):
    with open(path, 'r') as f:
        return yaml.safe_load(f)

def main():
    # Crear objeto Report vacío
    report = Report()

    protocol = loadProtocol()
    
    print("Bienvenido al simulador de capas de redes\n")

    mensaje, tasa_error, algoritmo, config = getMsg()
    print(f"\nMensaje ingresado: {mensaje}")
    
    binario = stringToBin(mensaje=mensaje, bits_per_char=protocol["bits_per_char"])
    print(f"Codificado ASCII: {binario}")
    
    try:
        msg_encode = applyAlgorithm(binario, algoritmo, config, report)
        print(f"Mensaje codificado: {msg_encode}")
    except ValueError as e:
        print(f"\n[ERROR] {e}")
        return
    
    msg_noise = make_noise(tasa_error, msg_encode)
    print(f"Message with sound: {msg_noise}")
    
    # Llenar datos del reporte
    report.setOriginalMessage(message=mensaje)
    report.setErrorRate(errorRate=tasa_error)
    if tasa_error > 0:
        report.setIsError(isError=True)
    else:
        report.setIsError(isError=False)
    report.setEncodedMessage(message=msg_encode)
    report.setSelectedAlgorithm(algorithm=algoritmo)
    report.setTransmittedMessage(message=msg_noise)
    report.updateErrorCount()
    report.exportToCsv(filename="test.csv")
    
    try:
        sendMsg(msg_noise)
        print(f"\n\t¡Mensaje enviado!")
    except ConnectionRefusedError:
        print(f"\n[ERROR] No se pudo establecer conexión con el receptor (mensaje no enviado).")
    except Exception as e:
        print(f"\n[ERROR inesperado] {e}")

if __name__ == "__main__":
    main()
