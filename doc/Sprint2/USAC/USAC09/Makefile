CC = riscv32-linux-gcc
CFLAGS = -Wall -Wextra -fanalyzer -g
ARCHFLAGS = -march=rv32imd -mabi=ilp32d
LIBRV32 = ${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

TARGET = prog.elf

OBJS = main.o median.o sort_array.o callfunc.o ../../unity.o

all: $(TARGET)

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) $(ARCHFLAGS) $(OBJS) -o $(TARGET)

main.o: main.c
	$(CC) $(CFLAGS) $(ARCHFLAGS) -I ../.. -c main.c -o main.o

%.o: %.s
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c $< -o $@

../../unity.o: ../../unity.c
	$(CC) $(CFLAGS) $(ARCHFLAGS) -I ../.. -c ../../unity.c -o ../../unity.o

clean:
	rm -f *.o *.elf

run: $(TARGET)
	qemu-riscv32 -L $(LIBRV32) ./$(TARGET)

.PHONY: all run clean