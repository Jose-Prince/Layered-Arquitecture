import random

def make_noise(error_rate, msg):
  new_msg = ""
  for bit in msg:
    if random.random() <= error_rate:
      bit = bit_contrary(bit)
    new_msg += bit

  return new_msg

def bit_contrary(bit):
  return '0' if bit == '1' else '1'
