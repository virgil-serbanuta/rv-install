import sys

def test(size):
  n = size
  s = 0
  while n > 0:
    s = s + n
    n = n + -1
  return s

if __name__ == '__main__':
  print (test(int(sys.argv[1])))
