function sum(size)
  local n = size
  local s = 0
  while n > 0 do
    s = s + n
    n = n + -1
  end
  print(s)
end
sum(tonumber(arg[1]))
