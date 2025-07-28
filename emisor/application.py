def getMsg():
    mensaje = input("Ingrese el mensaje a enviar: ").strip()
    tasa_error = float(input("Ingrese tasa de error (ej: 0.01 para 1%): ").strip())
    f_configuration = "0"
    
    opcion = ""
    while opcion not in ["0", "1", "2"]:
        print("Selecciona un algoritmo:")
        print("\t1. Algoritmo Hamming")
        print("\t2. Algoritmo Fletcher checksum")
        print("\t0. Salir")

        opcion = input("\tSeleccione una opción: ").strip()

        if opcion == "1":
            algoritmo = "hamming"
        elif opcion == "2":
            algoritmo = "fletcher"
            f_configuration = input("Configuración de fletcher a utilizar (16|32|64): ")
        elif opcion not in ["0", "1"]:
            print("\t\tOpción inválida. Intente de nuevo.\n")

    if opcion == "0":
        exit()

    return mensaje, tasa_error, algoritmo, int(f_configuration)
