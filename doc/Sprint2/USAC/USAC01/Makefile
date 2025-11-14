# Compilador e flags
CC = riscv32-linux-gcc
CFLAGS = -Wall -Wextra -fanalyzer -g
ARCHFLAGS = -march=rv32imd -mabi=ilp32d
LIBRV32 = ${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

# Binário final
PROGRAM = encrypt_test.elf

#===========================================================
# Build principal
#===========================================================
$(PROGRAM): main.o encrypt_data.o
	$(CC) $(CFLAGS) $(ARCHFLAGS) main.o encrypt_data.o -o $(PROGRAM)

#===========================================================
# Objetos
#===========================================================
main.o: main.c
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c

encrypt_data.o: encrypt_data.s
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c encrypt_data.s

#===========================================================
# Regras auxiliares
#===========================================================
run: $(PROGRAM)
	qemu-riscv32 -L $(LIBRV32) ./$(PROGRAM)

debug: $(PROGRAM)
	@echo "On other terminal window, run:"
	@echo "  riscv32-linux-gdb -tui ./$(PROGRAM)"
	qemu-riscv32 -L $(LIBRV32) -g 1234 $(PROGRAM) -S

clean:
	rm -f *.o *.elf
