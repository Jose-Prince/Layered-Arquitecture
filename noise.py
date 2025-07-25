import random

def make_noise(error_rate, msg):
  new_msg = ""
  for bit in msg:
    if random.random() <= error_rate:
      bit = bit_contrary(bit)
    new_msg = new_msg + str(bit)

  return new_msg

def bit_contrary(bit):
  if bit == 1:
    return 0
  else:
    return 1
