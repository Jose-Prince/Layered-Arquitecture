from application import getMsg
from presentation import stringToBin
from data_link import applyHamming
from noise import make_noise

def main():
    print("Bienvenido al simulador de capas de redes\n")

    mensaje, tasa_error, algoritmo = getMsg()
    print(f"\nMensaje ingresado: {mensaje}")
    
    binario = stringToBin(mensaje)
    print(f"Codificado ASCII: {binario}")
    
    msg_hamming = applyHamming(binario)
    print(f"Mensaje codificado: {msg_hamming}")
    
    msg_hamming = make_noise(tasa_error, msg_hamming)
    print(f"Message with sound: {msg_hamming}")

if __name__ == "__main__":
    main()
