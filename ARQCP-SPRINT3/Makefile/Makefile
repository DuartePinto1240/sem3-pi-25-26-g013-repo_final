# Makefile para Railway Station Management System
# ARQCP Sprint 3

CC = gcc
AS = as
CFLAGS = -Wall -Wextra -g -Iinclude
LDFLAGS = -lm

# Diretórios
SRC_DIR = src
ASM_DIR = asm
INC_DIR = include
OBJ_DIR = obj
BIN_DIR = bin
TEST_DIR = tests

# Criar diretórios se não existirem
$(shell mkdir -p $(OBJ_DIR) $(BIN_DIR))

# Ficheiros fonte C
C_SOURCES = $(wildcard $(SRC_DIR)/*.c)
C_OBJECTS = $(patsubst $(SRC_DIR)/%.c,$(OBJ_DIR)/%.o,$(C_SOURCES))

# Ficheiros assembly
ASM_SOURCES = $(wildcard $(ASM_DIR)/*.s)
ASM_OBJECTS = $(patsubst $(ASM_DIR)/%.s,$(OBJ_DIR)/%.o,$(ASM_SOURCES))

# Todos os objetos
OBJECTS = $(C_OBJECTS) $(ASM_OBJECTS)

# Executável principal
TARGET = $(BIN_DIR)/station_manager

# Testes
TEST_SOURCES = $(wildcard $(TEST_DIR)/*.c)
TEST_OBJECTS = $(patsubst $(TEST_DIR)/%.c,$(OBJ_DIR)/test_%.o,$(TEST_SOURCES))
TEST_BINS = $(patsubst $(TEST_DIR)/%.c,$(BIN_DIR)/test_%,$(TEST_SOURCES))

# Regra padrão
all: $(TARGET)

# Compilar programa principal
$(TARGET): $(OBJECTS)
	$(CC) $(OBJECTS) -o $@ $(LDFLAGS)
	@echo "Build concluído: $(TARGET)"

# Compilar ficheiros C
$(OBJ_DIR)/%.o: $(SRC_DIR)/%.c
	$(CC) $(CFLAGS) -c $< -o $@

# Compilar ficheiros assembly
$(OBJ_DIR)/%.o: $(ASM_DIR)/%.s
	$(AS) $< -o $@

# Compilar testes
$(OBJ_DIR)/test_%.o: $(TEST_DIR)/%.c
	$(CC) $(CFLAGS) -c $< -o $@

# Criar executáveis de teste
$(BIN_DIR)/test_%: $(OBJ_DIR)/test_%.o $(filter-out $(OBJ_DIR)/main.o, $(OBJECTS))
	$(CC) $^ -o $@ $(LDFLAGS)

# Compilar todos os testes
tests: $(TEST_BINS)
	@echo "Testes compilados"

# Executar testes
run-tests: tests
	@echo "A executar testes..."
	@for test in $(TEST_BINS); do \
		echo "==== Executando $$test ===="; \
		$$test; \
		echo ""; \
	done

# Executar programa principal
run: $(TARGET)
	./$(TARGET)

# Executar com ficheiro de configuração específico
run-config: $(TARGET)
	./$(TARGET) data/config.txt

# Limpar ficheiros compilados
clean:
	rm -rf $(OBJ_DIR) $(BIN_DIR)
	@echo "Limpeza concluída"

# Limpar e recompilar
rebuild: clean all

# Verificar dependências
check:
	@echo "=== Verificando ficheiros ==="
	@echo "Ficheiros C encontrados:"
	@ls -1 $(SRC_DIR)/*.c 2>/dev/null || echo "Nenhum"
	@echo ""
	@echo "Ficheiros Assembly encontrados:"
	@ls -1 $(ASM_DIR)/*.s 2>/dev/null || echo "Nenhum"
	@echo ""
	@echo "Ficheiros de teste encontrados:"
	@ls -1 $(TEST_DIR)/*.c 2>/dev/null || echo "Nenhum"

# Ajuda
help:
	@echo "=== Railway Station Management System - Makefile ==="
	@echo ""
	@echo "Alvos disponíveis:"
	@echo "  make            - Compila o programa principal"
	@echo "  make all        - Mesmo que 'make'"
	@echo "  make run        - Compila e executa o programa"
	@echo "  make run-config - Executa com ficheiro de config específico"
	@echo "  make tests      - Compila os testes"
	@echo "  make run-tests  - Compila e executa todos os testes"
	@echo "  make clean      - Remove ficheiros compilados"
	@echo "  make rebuild    - Limpa e recompila tudo"
	@echo "  make check      - Verifica ficheiros do projeto"
	@echo "  make help       - Mostra esta ajuda"
	@echo ""

.PHONY: all clean run run-config tests run-tests rebuild check help