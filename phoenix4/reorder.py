import csv
import argparse
import ipdb;

parser = argparse.ArgumentParser(description='Reorder csv file.')
parser.add_argument('--src', type=str)
parser.add_argument('--dest', type=str)

args = parser.parse_args()

with open(args.src, 'r') as infile:
   csvin = csv.reader(infile)
   with open(args.dest, 'w') as outfile:
      csvout = csv.writer(outfile)
      for row in csvin:
          csvout.writerow(row[1:] + row[:1])
       
       
