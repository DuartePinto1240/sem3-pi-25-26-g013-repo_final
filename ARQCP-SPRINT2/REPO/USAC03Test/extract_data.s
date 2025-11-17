# extract_data.S
# Implementação da função USAC03 para RV32 (32-bit)
#
# Assinatura C:
# int extract_data(char* str, char* token, char* unit, int* value)
#
# Convenção de chamada (RV32):
# a0: char* str
# a1: char* token
# a2: char* unit
# a3: int* value
# a0: valor de retorno (1 sucesso, 0 falha)

.section .text
.global extract_data

extract_data:
    # --- Prólogo (RV32) ---
    # Registos têm 4 bytes. Vamos guardar 9 registos (ra + s0-s7)
    # 9 * 4 = 36 bytes
    addi sp, sp, -36
    sw ra, 0(sp)      # Guardar endereço de retorno
    sw s0, 4(sp)      # s0 = str (base)
    sw s1, 8(sp)      # s1 = token
    sw s2, 12(sp)     # s2 = unit (ponteiro de saída)
    sw s3, 16(sp)     # s3 = value (ponteiro de saída)
    sw s4, 20(sp)     # s4 = p (ponteiro atual na string 'str')
    sw s5, 24(sp)     # s5 = acc (acumulador para o 'value')
    sw s6, 28(sp)     # s6 = p_token (ponteiro para o 'token' atual)
    sw s7, 32(sp)     # s7 = p_str_cmp (ponteiro para a 'str' durante a comparação)

    # Guardar os argumentos de entrada nos nossos registos salvos
    mv s0, a0
    mv s1, a1
    mv s2, a2
    mv s3, a3

    mv s4, s0           # s4 = p (ponteiro 'p' começa no início de 'str')

# --- Loop principal: Itera por cada registo "TOKEN&...#" ---
record_loop:
    lbu t0, 0(s4)       # t0 = *p
    beq t0, zero, fail  # Se *p == '\0', fim da string, token não encontrado

    # --- 1. Comparar o token ---
    mv s7, s4           # s7 = p_str_cmp = p
    mv s6, s1           # s6 = p_token = token
compare_token_loop:
    lbu t0, 0(s6)       # t0 = *p_token
    beq t0, zero, check_token_end # Se *p_token == '\0', chegámos ao fim do token

    lbu t1, 0(s7)       # t1 = *p_str_cmp
    bne t0, t1, next_record # Se *p_token != *p_str_cmp, não é este o token

    addi s7, s7, 1      # p_str_cmp++
    addi s6, s6, 1      # p_token++
    j compare_token_loop

check_token_end:
    # Chegámos ao fim do token. O próximo char em 'str' deve ser '&'
    lbu t0, 0(s7)       # t0 = *p_str_cmp
    li t1, '&'
    bne t0, t1, next_record # Não era "TOKEN&"

    addi s4, s7, 1      # s4 aponta para depois de "TOKEN&"
    j parse_unit

# --- 2. Procurar o próximo registo ---
next_record:
    lbu t0, 0(s4)       # t0 = *p
    beq t0, zero, fail  # Fim da string, falhou
    li t1, '#'
    beq t0, t1, found_hash # Encontrámos o separador

    addi s4, s4, 1      # p++
    j next_record

found_hash:
    addi s4, s4, 1      # Avançar 'p' para depois do '#'
    j record_loop       # Tentar comparar o token novamente

# --- 3. Extrair "unit:" ---
parse_unit:
    # Verificar se "unit:" existe
    lbu t0, 0(s4); li t1, 'u'; bne t0, t1, next_record
    lbu t0, 1(s4); li t1, 'n'; bne t0, t1, next_record
    lbu t0, 2(s4); li t1, 'i'; bne t0, t1, next_record
    lbu t0, 3(s4); li t1, 't'; bne t0, t1, next_record
    lbu t0, 4(s4); li t1, ':'; bne t0, t1, next_record

    addi s4, s4, 5      # Avançar 'p' para depois de "unit:"
    mv t2, s2           # t2 = ponteiro de saída 'unit'

copy_unit_loop:
    lbu t0, 0(s4)       # t0 = *p
    li t1, '&'
    beq t0, t1, unit_done # Fim da 'unit'
    beq t0, zero, fail    # Erro de formato (string acabou)

    sb t0, 0(t2)        # unit[i] = *p
    addi s4, s4, 1      # p++
    addi t2, t2, 1      # unit++
    j copy_unit_loop

unit_done:
    sb zero, 0(t2)      # Adicionar terminador nulo '\0' à 'unit'
    addi s4, s4, 1      # Avançar 'p' para depois do '&'

# --- 4. Extrair "value:" ---
parse_value:
    # Verificar se "value:" existe
    lbu t0, 0(s4); li t1, 'v'; bne t0, t1, next_record
    lbu t0, 1(s4); li t1, 'a'; bne t0, t1, next_record
    lbu t0, 2(s4); li t1, 'l'; bne t0, t1, next_record
    lbu t0, 3(s4); li t1, 'u'; bne t0, t1, next_record
    lbu t0, 4(s4); li t1, 'e'; bne t0, t1, next_record
    lbu t0, 5(s4); li t1, ':'; bne t0, t1, next_record

    addi s4, s4, 6      # Avançar 'p' para depois de "value:"

    li s5, 0            # s5 = acc = 0 (acumulador de valor)
    li t3, 0            # t3 = has_digit = 0 (flag)
    li t4, '0'
    li t5, '9'

parse_value_loop:
    lbu t0, 0(s4)       # t0 = *p
    blt t0, t4, value_done # t0 < '0'
    bgt t0, t5, value_done # t0 > '9'

    li t3, 1            # has_digit = 1
    sub t0, t0, t4      # Converter char '0'-'9' para int 0-9

    li t1, 10
    mul s5, s5, t1      # acc = acc * 10
    add s5, s5, t0      # acc = acc + digit

    addi s4, s4, 1      # p++
    j parse_value_loop

value_done:
    beq t3, zero, next_record # "value:" sem número, formato inválido

    lbu t0, 0(s4)
    li t1, '#'
    beq t0, t1, success # Se for '#', sucesso
    bne t0, zero, next_record # Se não for '#' NEM '\0', formato inválido

# --- 5. Sucesso ---
success:
    sw s5, 0(s3)        # *value = acc (Store Word, 32-bit)
    li a0, 1            # Retorno = 1
    j restore_and_return

# --- 6. Falha ---
fail:
    sb zero, 0(s2)      # unit[0] = '\0'
    sw zero, 0(s3)      # *value = 0
    li a0, 0            # Retorno = 0

# --- Epílogo (RV32) ---
restore_and_return:
    lw ra, 0(sp)      # Restaurar registos
    lw s0, 4(sp)
    lw s1, 8(sp)
    lw s2, 12(sp)
    lw s3, 16(sp)
    lw s4, 20(sp)
    lw s5, 24(sp)
    lw s6, 28(sp)
    lw s7, 32(sp)
    addi sp, sp, 36     # Desalocar o stack

    ret                 # Retornar para o chamador