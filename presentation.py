def stringToBin(mensaje):
    return ''.join(format(ord(c), '08b') for c in mensaje)
