# format_command.S

.section .text
.global format_command

format_command:
    # --- Prólogo ---
    addi sp, sp, -24
    sw ra, 0(sp)
    sw s0, 4(sp)      # s0 = ponteiro atual na string op
    sw s1, 8(sp)      # s1 = n
    sw s2, 12(sp)     # s2 = cmd (output)
    sw s3, 16(sp)     # s3 = char 1
    sw s4, 20(sp)     # s4 = char 2

    mv s0, a0
    mv s1, a1
    mv s2, a2

    # ==========================================
    # 1. Ler Primeiro Caractere (C1)
    # ==========================================
    jal read_next_char      # Chama rotina auxiliar para buscar próximo char não-espaço
    beq t0, zero, fail      # Se string acabou, falha

    # Capitalizar C1
    jal capitalize_char
    mv s3, t0               # s3 = C1

    # ==========================================
    # 2. Ler Segundo Caractere (C2)
    # ==========================================
    jal read_next_char
    beq t0, zero, fail      # Comandos precisam de pelo menos 2 letras

    # Capitalizar C2
    jal capitalize_char
    mv s4, t0               # s4 = C2

    # ==========================================
    # 3. Ler Terceiro Caractere (C3) - Decidir Fluxo
    # ==========================================
    jal read_next_char
    # Se t0 for 0 (fim da string), é um comando de 2 letras
    beq t0, zero, check_2char_cmd

    # Se temos um 3º char, pode ser GTH
    jal capitalize_char
    mv t5, t0               # t5 = C3
    j check_3char_cmd


# ==========================================
# Lógica para Comandos de 2 Letras (RE, YE, GE, RB)
# ==========================================
check_2char_cmd:
    # s3 = C1, s4 = C2

    # Verificar RE
    li t0, 'R'; bne s3, t0, try_YE
    li t0, 'E'; beq s4, t0, do_format_2char
    li t0, 'B'; beq s4, t0, do_format_2char # RB também começa com R
    j fail

try_YE:
    li t0, 'Y'; bne s3, t0, try_GE
    li t0, 'E'; beq s4, t0, do_format_2char
    j fail

try_GE:
    li t0, 'G'; bne s3, t0, fail
    li t0, 'E'; beq s4, t0, do_format_2char
    j fail

do_format_2char:
    # Validar número n (s1)
    blt s1, zero, fail      # n < 0
    li t0, 99
    bgt s1, t0, fail        # n > 99

    # Escrever Output: "C1C2,xx"
    sb s3, 0(s2)            # C1
    sb s4, 1(s2)            # C2
    li t0, ','
    sb t0, 2(s2)

    # Converter n para ascii
    li t0, 10
    divu t1, s1, t0         # Dezenas
    remu t2, s1, t0         # Unidades
    li t3, '0'
    add t1, t1, t3
    add t2, t2, t3

    sb t1, 3(s2)
    sb t2, 4(s2)
    sb zero, 5(s2)          # Null terminator
    j success


# ==========================================
# Lógica para Comandos de 3 Letras (GTH)
# ==========================================
check_3char_cmd:
    # s3=C1, s4=C2, t5=C3

    li t0, 'G'; bne s3, t0, fail
    li t0, 'T'; bne s4, t0, fail
    li t0, 'H'; bne t5, t0, fail

    # Validar que não há mais caracteres (deve ser o fim)
    # Precisamos de verificar se o resto da string é só espaços ou vazio
    # Guardar t5 na stack se precisarmos, mas aqui só queremos verificar o resto
    # s0 já aponta para o próximo

    # Truque: Chamar read_next_char. Se devolver algo que não seja 0, falha.
    # Mas precisamos de salvar o endereço de retorno atual se formos fazer jal aninhado?
    # Sim. Mas como estamos no nível principal, podemos apenas saltar para read e verificar.

    # Vamos fazer inline loop aqui para verificar fim
check_end_loop:
    lbu t0, 0(s0)
    beq t0, zero, do_format_GTH  # Fim encontrado, OK
    li t1, 32
    bne t0, t1, fail             # Se não for espaço e não for 0, erro (ex: GTHX)
    addi s0, s0, 1
    j check_end_loop

do_format_GTH:
    li t0, 'G'; sb t0, 0(s2)
    li t0, 'T'; sb t0, 1(s2)
    li t0, 'H'; sb t0, 2(s2)
    sb zero, 3(s2)
    j success


# ==========================================
# Rotinas Auxiliares (Inline para evitar complexidade de stack)
# ==========================================

success:
    li a0, 1
    j epilogue

fail:
    sb zero, 0(s2)          # String vazia
    li a0, 0
    j epilogue

epilogue:
    lw ra, 0(sp)
    lw s0, 4(sp)
    lw s1, 8(sp)
    lw s2, 12(sp)
    lw s3, 16(sp)
    lw s4, 20(sp)
    addi sp, sp, 24
    ret

# --- Rotina: Ler Próximo Char Não-Espaço ---
# Input: s0 (ponteiro atual)
# Output: t0 (char encontrado ou 0), s0 atualizado
read_next_char:
    lbu t0, 0(s0)
    addi s0, s0, 1          # Avançar sempre
    beq t0, zero, rnc_end   # Fim da string
    li t1, 32               # Espaço
    beq t0, t1, read_next_char # Se for espaço, repetir
rnc_end:
    ret                     # Retorna com o char em t0

# --- Rotina: Capitalizar ---
# Input: t0 (char)
# Output: t0 (char capitalizado)
capitalize_char:
    li t1, 'a'
    blt t0, t1, cap_end
    li t1, 'z'
    bgt t0, t1, cap_end
    addi t0, t0, -32        # Converter para maiúscula
cap_end:
    ret