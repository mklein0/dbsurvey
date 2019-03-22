#
import csv
import itertools
import random
from string import ascii_letters

len_letters = len(ascii_letters)


def wide_data():
    return list(itertools.chain.from_iterable(
        # INT, STR, FLOAT
        [
            random.randrange(0, 1000),
            ''.join(ascii_letters[random.randrange(0, len_letters)] for _ in range(16)),
            random.randrange(0, 500, step=0.5, _int=float),
        ]
        for i in range(3000)
    ))


with open('WIDE_TABLE.csv', 'w') as outfile:
    csvwriter = csv.writer(outfile)
    with open('../web_stat/WEB_STAT.csv', 'rb') as infile:
        csvreader = csv.reader(infile)
        for inrow in csvreader:
            row = inrow[:-1] + wide_data() + inrow[-1:]
            csvwriter.writerow(row)
