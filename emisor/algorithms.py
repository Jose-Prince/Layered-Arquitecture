import os
import yaml
class HammingAlgorithm:
    def __init__(self, msg, config_path="./protocol.yaml", generate_report=True, output_path="./reports/t_hamming_report.txt", detail_path="./reports/t_hamming_detail.txt"):
        self.msg = msg
        self.msg_bits = []
        self.data_bits = len(msg)
        self.parity_bits = 0
        self.pos_redundancy_bits = []
        self.value_redundancy_bits = {}
        self.type_bit = ["d", "r", "rg"]  # d: data, r: hamming redundancy, rg: global redundancy
        
        # Otros
        self.detail_lines = []
        self.output_path = output_path
        self.detail_path = detail_path

        # Leer configuración del archivo YAML
        config = self.loadConfig(config_path)
        self.is_extended = config.get("extended", True)
        self.is_even_parity = config.get("parityEven", True)
        self.global_redundancy_bits = 1 if self.is_extended else 0
        self.bit_algorithm = config.get('algorithms', {}).get('hamming', 0)
        self.bits_configuration = config.get('hamming', {}).get('bits_configuration', 5)
        
        self.calculateParityBits()
        self.quantity_bits = self.data_bits + self.parity_bits + self.global_redundancy_bits

        self.positionRedundancyBits()
        self.calculateParityBitsValues()
        self.createMsg()
        self.setAllParityBits()
        self.msg_output = self.getMsgOutput()
        
        if generate_report:
            self.exportToTxt(filename=self.output_path)
            self.exportDetailTxt(detail_path=self.detail_path)
        
    def loadConfig(self, path):
        with open(path, 'r') as file:
            return yaml.safe_load(file)

    def calculateParityBits(self):
        while (2 ** self.parity_bits < (self.data_bits + self.parity_bits + 1)):
            self.parity_bits += 1

    def positionRedundancyBits(self):
        position_temp = 0
        max_position = self.quantity_bits - 1 if self.is_extended else self.quantity_bits
        while (2 ** position_temp <= max_position):
            self.pos_redundancy_bits.append(2 ** position_temp)
            position_temp += 1

    def calculateParityBitsValues(self):
        for r in self.pos_redundancy_bits:
            self.value_redundancy_bits[r] = []
            for i in range(1, self.quantity_bits + 1):
                if i & r != 0 and (not self.is_extended or i != self.quantity_bits):
                    self.value_redundancy_bits[r].append(i)

    def createMsg(self):
        j = 0
        self.msg_bits = []

        self.detail_lines.append("Construcción del mensaje codificado:")
        
        for i in range(1, self.quantity_bits + 1):
            if self.is_extended and i == self.quantity_bits:
                self.msg_bits.append((None, self.type_bit[2]))  # bit de paridad global (al final)
                self.detail_lines.append(f"- Posición {i}: reservado para bit de paridad global (rg)")
            elif i in self.pos_redundancy_bits:
                self.msg_bits.append((None, self.type_bit[1]))  # bit de paridad hamming
                self.detail_lines.append(f"- Posición {i}: reservado para bit de paridad Hamming (r)")
            else:
                bit = int(self.msg[j])
                self.msg_bits.append((bit, self.type_bit[0]))  # bit de datos
                self.detail_lines.append(f"- Posición {i}: bit de datos '{bit}' asignado desde msg[{j}]")
                j += 1

        self.detail_lines.append("")  # Espacio visual

    def calculateParity(self, position):
        values = self.value_redundancy_bits[position]
        bits_in_positions = [(i, self.msg_bits[i - 1][0]) for i in values]
        count_ones = sum(1 for _, bit in bits_in_positions if bit == 1)
        parity = count_ones % 2 if self.is_even_parity else (count_ones + 1) % 2

        bit_values_str = ", ".join(f"{i}={bit}" for i, bit in bits_in_positions)

        self.detail_lines.append(
            f"r{position} cubre posiciones {values} -> valores: [{bit_values_str}]. "
            f"Total de 1s: {count_ones}. Paridad {'par' if self.is_even_parity else 'impar'} usada -> bit de paridad: {parity}"
        )

        return parity

    def calculateParityExtend(self):
        bits_in_positions = [(i + 1, self.msg_bits[i][0]) for i in range(self.quantity_bits - 1)]
        count_ones = sum(1 for _, bit in bits_in_positions if bit == 1)
        parity = count_ones % 2 if self.is_even_parity else (count_ones + 1) % 2

        bit_values_str = ", ".join(f"{i}={bit}" for i, bit in bits_in_positions)

        self.detail_lines.append(
            f"Paridad extendida cubre todas las posiciones excepto la final -> valores: [{bit_values_str}]. "
            f"Total de 1s: {count_ones}. Paridad {'par' if self.is_even_parity else 'impar'} usada -> bit global: {parity}"
        )

        return parity


    def setAllParityBits(self):
        # 1. Calcular paridades Hamming
        for position in self.pos_redundancy_bits:
            parity = self.calculateParity(position)
            self.msg_bits[position - 1] = (parity, self.type_bit[1])

        # 2. Calcular paridad extendida si está activada
        if self.is_extended:
            parity_extend = self.calculateParityExtend()
            self.msg_bits[-1] = (parity_extend, self.type_bit[2])

    def getMsgOutput(self):
        bits_dc = format(self.data_bits, f'0{self.bits_configuration}b')
        return str(self.bit_algorithm) + bits_dc + ''.join(str(bit) for bit, _ in self.msg_bits)

    def exportToTxt(self, filename):
        os.makedirs(os.path.dirname(filename), exist_ok=True)
        with open(filename, "w", encoding="utf-8") as f:
            f.write("===== HAMMING TRANSMITTER REPORT =====\n\n")

            f.write("Configuración:\n")
            f.write(f"- Protocolo de data: ({self.quantity_bits},{self.data_bits})\n")
            f.write(f"- Paridad usada: {'par' if self.is_even_parity else 'impar'}\n")
            f.write(f"- Paridad extendida: {'sí' if self.is_extended else 'no'}\n")
            f.write(f"- Bits de datos: {self.data_bits}\n")
            f.write(f"- Bits de paridad Hamming: {self.parity_bits}\n")
            if self.is_extended:
                f.write(f"- Bits de redundancia global (extendido): {self.global_redundancy_bits}\n")
            f.write(f"- Bits totales (codificados): {self.quantity_bits}\n")
            f.write(f"- Posiciones de paridad Hamming: {self.pos_redundancy_bits}\n")
            if self.is_extended:
                f.write(f"- Posición de bit de paridad global (extendido): {self.quantity_bits}\n")
            f.write("\n")

            f.write("Mensaje recibido (bits de datos):\n")
            f.write(f"{self.msg}\n\n")

            f.write("Mensaje codificado (msg_bits):\n")
            f.write("Posición\tBit\tTipo\n")
            for i, (val, tipo) in enumerate(self.msg_bits, start=1):
                f.write(f"{i}\t\t{val if val is not None else '?'}\t{tipo}\n")
            f.write("\n")

            f.write("Cobertura de cada bit de paridad Hamming:\n")
            for r, positions in self.value_redundancy_bits.items():
                f.write(f"r{r} (pos {r}) -> cubre: {positions}\n")
            f.write("\n")

            f.write("Mensaje binario final (para transmisión):\n")
            final = ''.join(str(bit if bit is not None else 'X') for bit, _ in self.msg_bits)
            f.write(final + "\n")

            f.write("\n===== FIN DEL REPORTE =====\n")

    def exportDetailTxt(self, detail_path):
        os.makedirs(os.path.dirname(detail_path), exist_ok=True)
        with open(detail_path, "w", encoding="utf-8") as f:
            f.write("===== DETALLE DE LA CODIFICACIÓN HAMMING =====\n\n")
            for line in self.detail_lines:
                f.write(line + "\n")
            f.write("\n===== FIN DEL DETALLE =====\n")

def fletcher_checksum_emitter(msg, f_type):
    with open("../protocol.yaml", 'r') as file:
        config = yaml.safe_load(file)
        
    algorithm_type = config.get('algorithms', {}).get('fletcher', 1)

    block_size = f_type // 2
    mod = 2 ** block_size - 1

    # Padding para que el mensaje sea divisible por el block_size
    padding_needed = block_size - (len(msg) % block_size)
    padded_msg = msg
    if padding_needed != block_size:
        padded_msg += "0" * padding_needed

    sum1 = 0
    sum2 = 0

    # Procesamiento por bloques
    for i in range(0, len(padded_msg), block_size):
        c = padded_msg[i:i + block_size]
        value = int(c, 2)
        sum1 = (sum1 + value) % mod
        sum2 = (sum2 + sum1) % mod

    # Calcular el checksum
    checksum = (sum2 << block_size) | sum1
    total_bits = 2 * block_size

    # Convertir a binario
    bin_checksum = format(checksum, f'0{total_bits}b')

    complete_msg = str(algorithm_type) + padded_msg + bin_checksum
    print(f"Checksum: {complete_msg}")
    return complete_msg


def menu():
    report_path="./reports/t_hamming_report.txt"
    config_path="./protocol.yaml"
    detail_path="./reports/t_hamming_detail.txt"

    with open(config_path, "r") as file:
        config = yaml.safe_load(file)

    extended = config.get("extended", True)
    parity_type = config.get("parity", "even")

    print("===== TRANSMISOR HAMMING =====\n")
    print(f"Se utilizará la configuración definida en el archivo: '{os.path.abspath(config_path)}'\n")
    print("Configuración cargada:")
    print(f"- Paridad: {'par' if parity_type == 'even' else 'impar'}")
    print(f"- Paridad extendida: {'sí' if extended else 'no'}\n")

    print("\tHint: Si deseas modificar el tipo de paridad (even/odd) o activar/desactivar la paridad extendida (true/false), edita el archivo 'protocol.yaml'\n")

    print("Ingrese los datos para codificar con Hamming:\n")

    # Solicitar mensaje binario
    msg_valido = False
    msg = ""

    while not msg_valido:
        msg = input("Ingrese el mensaje binario (solo 0s y 1s): ").strip()
        msg_valido = all(c in "01" for c in msg) and len(msg) > 0
        if not msg_valido:
            print("Entrada inválida. Solo se permiten 0s y 1s.")
    
    # Crear transmisor
    transmitter = HammingAlgorithm(
        msg=msg,
        config_path=config_path,
        generate_report=True,
        output_path=report_path,
        detail_path=detail_path
    )

    # Mostrar mensaje final codificado y resumen
    print(f"Codificación final del mensaje: {transmitter.msg_output}")
    print(f"Protocolo de data: ({transmitter.quantity_bits},{transmitter.data_bits})", "\n")
    
    print(f"\tReporte generado en: '{os.path.abspath(report_path)}'")
    print(f"\tDetalle de los pasos seguidos, generado en: '{os.path.abspath(detail_path)}'")

if __name__ == "__main__":
  menu()
