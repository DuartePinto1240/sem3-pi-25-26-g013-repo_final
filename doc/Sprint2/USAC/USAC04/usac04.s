# format_command.S
# Implementação da função USAC04 para RV32 (32-bit)
#
# Assinatura C:
# int format_command(char* op, int n, char *cmd)
#
# Convenção de chamada (RV32):
# a0: char* op
# a1: int n
# a2: char* cmd
# a0: valor de retorno (1 sucesso, 0 falha)

.section .text
.global format_command

format_command:
    # --- Prólogo (RV32) ---
    addi sp, sp, -20
    sw ra, 0(sp)      # Endereço de retorno
    sw s0, 4(sp)      # s0 = *op
    sw s1, 8(sp)      # s1 = n
    sw s2, 12(sp)     # s2 = *cmd
    sw s3, 16(sp)     # s3 = char 1 (capitalizado)

    # Copiar argumentos para registos salvos
    mv s0, a0
    mv s1, a1
    mv s2, a2

    # --- 1. Trim & Capitalize ---
    li t1, ' '
find_first_char:
    lbu t0, 0(s0)       # t0 = *op
    beq t0, zero, fail  # Se a string está vazia ou só tem espaços, falha
    beq t0, t1, is_space
    j found_first_char
is_space:
    addi s0, s0, 1      # op++
    j find_first_char

found_first_char:
    # t0 contém o primeiro char não-espaço
    # Capitalizar t0
    li t2, 'a'
    blt t0, t2, check_upper_Z_1 # Se t0 < 'a', não é minúscula
    li t2, 'z'
    bgt t0, t2, not_lower_1     # Se t0 > 'z', não é minúscula

    # É minúscula, capitaliza (t0 = t0 - 32)
    addi t0, t0, -32    # <-- CORREÇÃO AQUI
not_lower_1:
check_upper_Z_1:
    mv s3, t0                   # s3 = char 1 (capitalizado)

    # Ler e capitalizar char 2
    addi s0, s0, 1
    lbu t0, 0(s0)
    li t2, 'a'
    blt t0, t2, check_upper_Z_2
    li t2, 'z'
    bgt t0, t2, not_lower_2

    addi t0, t0, -32    # <-- CORREÇÃO AQUI
not_lower_2:
check_upper_Z_2:
    mv s4, t0                   # s4 = char 2 (capitalizado)

    # Ler e capitalizar char 3
    addi s0, s0, 1
    lbu t0, 0(s0)
    li t2, 'a'
    blt t0, t2, check_upper_Z_3
    li t2, 'z'
    bgt t0, t2, not_lower_3

    addi t0, t0, -32    # <-- CORREÇÃO AQUI
not_lower_3:
check_upper_Z_3:
    mv s5, t0                   # s5 = char 3 (capitalizado)

    # --- 2. Comparar o comando ---

    # É "GTH"? (s3='G', s4='T', s5='H')
    li t0, 'G'
    bne s3, t0, check_RE
    li t0, 'T'
    bne s4, t0, check_RE
    li t0, 'H'
    bne s5, t0, check_RE

    # É "GTH". Verificar se o 4º char é espaço ou nulo
    addi s0, s0, 1
    lbu t0, 0(s0)
    beq t0, t1, format_GTH  # t1 = ' '
    beq t0, zero, format_GTH
    j fail                  # Se for "GTHX", falha

check_RE:
    # É "RE"? (s3='R', s4='E')
    li t0, 'R'
    bne s3, t0, check_YE
    li t0, 'E'
    bne s4, t0, check_YE
    j check_2char_end

check_YE:
    # É "YE"? (s3='Y', s4='E')
    li t0, 'Y'
    bne s3, t0, check_GE
    li t0, 'E'
    bne s4, t0, check_GE
    j check_2char_end

check_GE:
    # É "GE"? (s3='G', s4='E')
    li t0, 'G'
    bne s3, t0, check_RB
    li t0, 'E'
    bne s4, t0, check_RB
    j check_2char_end

check_RB:
    # É "RB"? (s3='R', s4='B')
    li t0, 'R'
    bne s3, t0, fail
    li t0, 'B'
    bne s4, t0, fail
    j check_2char_end

check_2char_end:
    # s5 contém o 3º char. Deve ser espaço ou nulo.
    beq s5, t1, format_2char  # t1 = ' '
    beq s5, zero, format_2char
    j fail                    # Se for "REX", falha

    # --- 3. Formatar "CMD,xx" ---
format_2char:
    # s1 = n. Verificar se n está no range [0, 99]
    blt s1, zero, fail        # Se n < 0, falha
    li t0, 99
    bgt s1, t0, fail        # Se n > 99, falha

    # Escrever os dois chars do CMD
    sb s3, 0(s2)            # cmd[0] = char 1
    sb s4, 1(s2)            # cmd[1] = char 2

    # Escrever a vírgula
    li t0, ','
    sb t0, 2(s2)            # cmd[2] = ','

    # Converter 'n' (em s1) para dois dígitos
    li t0, 10
    divu t1, s1, t0         # t1 = n / 10 (dezenas)
    remu t2, s1, t0         # t2 = n % 10 (unidades)

    li t3, '0'
    add t1, t1, t3          # Converter dezenas para char
    add t2, t2, t3          # Converter unidades para char

    sb t1, 3(s2)            # cmd[3] = char(dezenas)
    sb t2, 4(s2)            # cmd[4] = char(unidades)

    # Adicionar terminador nulo
    sb zero, 5(s2)          # cmd[5] = '\0'
    j success

    # --- 4. Formatar "GTH" ---
format_GTH:
    li t0, 'G'
    sb t0, 0(s2)
    li t0, 'T'
    sb t0, 1(s2)
    li t0, 'H'
    sb t0, 2(s2)
    sb zero, 3(s2)          # cmd[3] = '\0'
    j success

    # --- 5. Saídas ---
fail:
    sb zero, 0(s2)          # *cmd = '\0'
    li a0, 0                # Retorno = 0
    j epilogue

success:
    li a0, 1                # Retorno = 1
    j epilogue

epilogue:
    # Restaurar registos
    lw ra, 0(sp)
    lw s0, 4(sp)
    lw s1, 8(sp)
    lw s2, 12(sp)
    lw s3, 16(sp)
    addi sp, sp, 20         # Desalocar stack
    ret                     # Retornar
