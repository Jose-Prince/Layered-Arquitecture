from algorithms import HammingAlgorithm
from algorithms import fletcher_checksum_emitter

def applyAlgorithm(mensaje_binario, algorithm, fletcher_type, report):
    if algorithm == "hamming":
      transmitter = HammingAlgorithm(msg=mensaje_binario, config_path="../protocol.yaml", generate_report=True, output_path="../reports/report_emisor.txt", detail_path="../reports/detail_emisor.txt", report=report)
      return transmitter.msg_output
    else:
      return fletcher_checksum_emitter(mensaje_binario, fletcher_type)
