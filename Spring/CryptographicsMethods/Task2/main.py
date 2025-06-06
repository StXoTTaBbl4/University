msg = [int(x) for x in list("01001110101110100000")]
keys = [[int(x) for x in list("1011110100")], [int(x) for x in list("0101000101")]]

size = len(msg)

steps = 2

right = []
left = []

for i in range(steps):
    left = msg[:int(size / 2)]
    right = msg[int(size/2):]

    xor_result = []
    for j in range(len(right)):
        xor_result.append(left[j] ^ keys[i][j] ^ right[j])

    right.extend(xor_result)

    msg = right

print("answer")
print(''.join(str(x) for x in msg))
