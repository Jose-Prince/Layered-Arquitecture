from application import getMsg
from presentation import stringToBin
from data_link import applyAlgorithm 
from noise import make_noise
from transmission import sendMsg
import yaml

def loadProtocol(path="../protocol.yaml"):
    with open(path, 'r') as f:
        return yaml.safe_load(f)

def main():
    protocol = loadProtocol()
    
    print("Bienvenido al simulador de capas de redes\n")

    mensaje, tasa_error, algoritmo, config = getMsg()
    print(f"\nMensaje ingresado: {mensaje}")
    
    binario = stringToBin(mensaje=mensaje, bits_per_char=protocol["bits_per_char"])
    print(f"Codificado ASCII: {binario}")
    
    try:
        msg_hamming = applyAlgorithm(binario, algoritmo, config)
        print(f"Mensaje codificado: {msg_hamming}")
    except ValueError as e:
        print(f"\n[ERROR] {e}")
        return
    
    msg_hamming = make_noise(tasa_error, msg_hamming)
    print(f"Message with sound: {msg_hamming}")
    
    try:
        sendMsg(msg_hamming)
        print(f"\n\t¡Mensaje enviado!")
    except ConnectionRefusedError:
        print(f"\n[ERROR] No se pudo establecer conexión con el receptor (mensaje no enviado).")
    except Exception as e:
        print(f"\n[ERROR inesperado] {e}")

if __name__ == "__main__":
    main()
