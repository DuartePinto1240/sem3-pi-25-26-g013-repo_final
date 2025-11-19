# Makefile Limpo para USAC01
CC = riscv32-linux-gcc
CFLAGS = -Wall -Wextra -fanalyzer -g
ARCHFLAGS = -march=rv32imd -mabi=ilp32d
LIBRV32 = ${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

# Nome do executável
TARGET = prog.elf

# LISTA DE OBJETOS:
# 1. main.o (o teste)
# 2. asm.o (o teu código - renomeia encrypt_data.s para asm.s!)
# 3. callfunc.o (auxiliar do professor)
# 4. ../../unity.o (biblioteca de testes que está 2 pastas acima)
OBJS = main.o asm.o callfunc.o ../../unity.o

all: $(TARGET)

# Cria o executável juntando tudo
$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) $(ARCHFLAGS) $(OBJS) -o $(TARGET)

# Regra para compilar .c (main.c)
# Inclui -I ../.. para encontrar o unity.h
main.o: main.c
	$(CC) $(CFLAGS) $(ARCHFLAGS) -I ../.. -c main.c -o main.o

# Regra para compilar .s (asm.s e callfunc.s)
%.o: %.s
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c $< -o $@

# Regra especial para compilar o Unity que está longe
../../unity.o: ../../unity.c
	$(CC) $(CFLAGS) $(ARCHFLAGS) -I ../.. -c ../../unity.c -o ../../unity.o

clean:
	rm -f *.o *.elf

run: $(TARGET)
	qemu-riscv32 -L $(LIBRV32) ./$(TARGET)

.PHONY: all run clean