s = ""
f = open("words.txt")
for line in f:
    s += line
f.close()

words = s[1:-1].split("\",\"")
filtered_words=[]
for num, word in enumerate(words):
    if (len(word) <= 5):
        continue
    filtered_words.append(word)
print(filtered_words, len(filtered_words))

f = open("filtered_words.txt", 'w')
for word in filtered_words:
    f.write(word + '\n')
f.close()

