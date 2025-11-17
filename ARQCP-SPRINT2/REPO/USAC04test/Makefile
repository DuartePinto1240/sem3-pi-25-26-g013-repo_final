# Makefile para USAC04
# Define o compilador 32-bit
CC = riscv32-linux-gcc

# Flags de compilação
CFLAGS = -Wall -Wextra -fanalyzer -g
ARCHFLAGS = -march=rv32imd -mabi=ilp32d

# Caminho para o sysroot (bibliotecas) do QEMU
LIBRV32 = ${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

# Nome do executável final
TARGET = usac04.elf

# Ficheiros objeto necessários
OBJS = main.o format_command.o

# Regra 'all': O que fazer quando executas 'make'
all: $(TARGET)

# Regra para ligar (linkar) o programa final
$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) $(ARCHFLAGS) $(OBJS) -o $(TARGET)

# Regra para compilar o ficheiro C
main.o: main.c
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c

# Regra para montar (assemble) o ficheiro Assembly
format_command.o: format_command.S
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c format_command.S

# Regra 'clean': Limpa os ficheiros gerados
clean:
	rm -f *.o *.elf

# Regra 'run': Executa o programa com QEMU
run: $(TARGET)
	qemu-riscv32 -L $(LIBRV32) ./$(TARGET)

# Informa ao 'make' que 'all', 'run', e 'clean' não são nomes de ficheiros
.PHONY: all run clean
