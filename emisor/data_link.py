from algorithms import HammingAlgorithm

def applyHamming(mensaje_binario):
    transmitter = HammingAlgorithm(msg=mensaje_binario, config_path="../protocol.yaml", generate_report=False, output_path="../reports/report_emisor.txt", detail_path="../reports/detail_emisor.txt")
    return transmitter.msg_output
