# Makefile Limpo para USAC04
CC = riscv32-linux-gcc
CFLAGS = -Wall -Wextra -fanalyzer -g
ARCHFLAGS = -march=rv32imd -mabi=ilp32d
LIBRV32 = ${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

# Nome do executável
TARGET = prog.elf

# LISTA DE FICHEIROS A COMPILAR
# 1. main.o (O teste)
# 2. unity.o (A biblioteca de testes - tem de estar na pasta!)
# 3. callfunc.o (Ficheiro auxiliar)
# 4. format_command.o (O teu código Assembly)
OBJS = main.o unity.o callfunc.o format_command.o

all: $(TARGET)

# Regra para criar o executável
$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) $(ARCHFLAGS) $(OBJS) -o $(TARGET)

# Regra para ficheiros .c
%.o: %.c
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c $< -o $@

# Regra para ficheiros .s (como callfunc.s)
%.o: %.s
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c $< -o $@

# Regra para ficheiros .S (como format_command.S)
%.o: %.S
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c $< -o $@

clean:
	rm -f *.o *.elf

run: $(TARGET)
	qemu-riscv32 -L $(LIBRV32) ./$(TARGET)

.PHONY: all run clean