def getMsg():
    mensaje = input("Ingrese el mensaje a enviar: ").strip()
    tasa_error = float(input("Ingrese tasa de error (ej: 0.01 para 1%): ").strip())
    
    opcion = ""
    while opcion not in ["0", "1"]:
        print("Selecciona un algoritmo:")
        print("\t1. Algoritmo Hamming")
        print("\t2. Otro algoritmo (en implementación)")
        print("\t0. Salir")

        opcion = input("\tSeleccione una opción: ").strip()

        if opcion == "2":
            print("\t\tEn implementación.\n")
        elif opcion not in ["0", "1"]:
            print("\t\tOpción inválida. Intente de nuevo.\n")

    if opcion == "0":
        exit()

    
    algoritmo = "hamming"
    return mensaje, tasa_error, algoritmo
