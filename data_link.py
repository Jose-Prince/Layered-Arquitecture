from transmitter import HammingTransmitter

def applyHamming(mensaje_binario):
    transmitter = HammingTransmitter(mensaje_binario, generate_report=False)
    return transmitter.msg_output
