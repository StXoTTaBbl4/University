S = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
key = [3, 6, 7, 9, 4, 1, 2, 5, 8, 0]
j = 0
for i in range(10):
    j = (j + S[i] + key[i]) % 10
    S[i], S[j] = S[j], S[i]  # swap

i = 0
j = 0
K = []
for _ in range(3):
    i = (i + 1) % 10
    j = (j + S[i]) % 10
    S[i], S[j] = S[j], S[i]  # swap
    t = (S[i] + S[j]) % 10
    K.append(S[t])
K.reverse()
print(K)