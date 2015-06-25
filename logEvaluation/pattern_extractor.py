p = open('pattern.txt', 'w')

with open('C:\Users\Jonas\OneDrive\Projects\ConstraintsFromTemplates\constraints1.csv') as f:
    for line in f:
        if line.find('pattern') != -1:
            start = line.find('pattern') + 13
            end = line.find('""', start)
            pattern = str(line[start:end])
            pattern = pattern.replace('\\\\', '\\')
            p.write(str(pattern) + '\n')

p.close()