def stringToBin(mensaje, bits_per_char):
    return ''.join(format(ord(c), f'0{bits_per_char}b') for c in mensaje)
