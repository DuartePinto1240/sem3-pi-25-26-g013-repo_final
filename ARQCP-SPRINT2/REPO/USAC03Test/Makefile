CC = riscv32-linux-gcc
CFLAGS = -Wall -Wextra -fanalyzer -g
ARCHFLAGS = -march=rv32imd -mabi=ilp32d
LIBRV32 = ${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

# Nome do executável final
TARGET = prog.elf

# LISTA DE FICHEIROS A COMPILAR
# 1. main.o (O teste)
# 2. unity.o (A biblioteca de testes - que está na tua pasta)
# 3. callfunc.o (Ficheiro auxiliar)
# 4. extract_data.o (O teu código Assembly)
OBJS = main.o unity.o callfunc.o extract_data.o

all: $(TARGET)

# Regra para criar o executável
$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) $(ARCHFLAGS) $(OBJS) -o $(TARGET)

# Regra para ficheiros .c (main.c, unity.c)
%.o: %.c
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c $< -o $@

# Regra para ficheiros .s (callfunc.s)
%.o: %.s
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c $< -o $@

# Regra para ficheiros .S (extract_data.S - S maiúsculo)
%.o: %.S
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c $< -o $@

clean:
	rm -f *.o *.elf

run: $(TARGET)
	qemu-riscv32 -L $(LIBRV32) ./$(TARGET)

.PHONY: all run clean