# Define o compilador 32-bit
CC = riscv32-linux-gcc

# Flags de compilação
CFLAGS = -Wall -Wextra -fanalyzer -g
ARCHFLAGS = -march=rv32imd -mabi=ilp32d

# Caminho para o sysroot (bibliotecas) do QEMU
LIBRV32 = ${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

# Nome do executável final
TARGET = usac03.elf

# Ficheiros objeto necessários
OBJS = main.o extract_data.o

# Regra 'all': O que fazer quando executas 'make'
all: $(TARGET)

# Regra para ligar (linkar) o programa final
$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) $(ARCHFLAGS) $(OBJS) -o $(TARGET)

# Regra para compilar o ficheiro C
main.o: main.c
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c

# Regra para montar (assemble) o ficheiro Assembly
extract_data.o: extract_data.S
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c extract_data.S

# Regra 'clean': Limpa os ficheiros gerados
clean:
	rm -f *.o *.elf

# Regra 'run': Executa o programa com QEMU
run: $(TARGET)
	qemu-riscv32 -L $(LIBRV32) ./$(TARGET)

# Informa ao 'make' que 'all', 'run', e 'clean' não são nomes de ficheiros
.PHONY: all run clean
