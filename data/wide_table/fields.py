#
import csv
import itertools
import random
from string import ascii_letters

len_letters = len(ascii_letters)


def wide_data(num_sets):
    return list(itertools.chain.from_iterable(
        # INT, STR, FLOAT
        [
            random.randrange(0, 1000),
            ''.join(ascii_letters[random.randrange(0, len_letters)] for _ in range(16)),
            random.randrange(0, 1000) * 0.5,
        ]
        for i in range(num_sets)
    ))


number = 100000
label = '100K'

with open('WIDE_TABLE_{}.csv'.format(label), 'w') as outfile:
    csvwriter = csv.writer(outfile)
    with open('../web_stat/WEB_STAT.csv', 'r') as infile:
        csvreader = csv.reader(infile)
        for inrow in csvreader:
            num_columns = number - len(inrow)
            num_sets = (num_columns + 2) // 3
            print((number, num_columns, num_sets, 3*num_sets+len(inrow)))
            row = inrow[:-1] + wide_data(num_sets) + inrow[-1:]
            csvwriter.writerow(row)
