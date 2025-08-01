import socket
import yaml

def loadNetworkConfig(path="../protocol.yaml"):
    with open(path, 'r') as f:
        return yaml.safe_load(f)["network"]
    
    
def sendMsg(msg):
    config = loadNetworkConfig()
    host = config["host"]
    port = config["port"]
    # print(f"\t\tConectando a {host}:{port}...")
    
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect((host, port))
        s.sendall(msg.encode())
        # print(f"\tMensaje enviadoal servidor: '{msg}'")