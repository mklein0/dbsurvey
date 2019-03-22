
int_field = 'FILLER.INT{:04d} BIGINT,'
str_field = 'FILLER.STR{:04d} VARCHAR,'
float_field = 'FILLER.FLOAT{:04d} FLOAT,'

for i in range(3000):
    print(int_field.format(i))
    print(str_field.format(i))
    print(float_field.format(i))
