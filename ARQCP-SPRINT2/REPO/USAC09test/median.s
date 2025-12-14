.section .text
.global median
.extern sort_array

# int median(int* vec, int length, int *me)
# a0 = vec, a1 = length, a2 = me

median:
    addi sp, sp, -16
    sw ra, 12(sp)        # guarda radress
    sw a0, 0(sp)         # guarda vec eturn na stack
    sw a1, 4(sp)         # guarda length na stack
    sw a2, 8(sp)         # guarda me na stack

    blez a1, invalid     # if (length <= 0) return 0

    li a2, 1             # a2 = 1 para passar como argumento
    jal sort_array       # chama função sort_array

    # restaura parâmetros originais
    lw a0, 0(sp)
    lw a1, 4(sp)
    lw a2, 8(sp)

    li t0,2              # div = 2
    rem t1, a1, t0       # length % 2
    beqz t1, even_case   # if (length % 2 == 0)

odd_case:
    div t2, a1, t0       # length / 2
    slli t3, t2, 2       # offset * 4
    add t4, a0, t3       # &vec[length/2]
    lw t5, 0(t4)         # vec[length/2]
    sw t5, 0(a2)         # *me = valor
    li a0, 1             # return 1
    j end

even_case:
    div t2, a1, t0       # length / 2
    addi t3, t2, -1      # lenght/2 -1
    slli t4, t2, 2       # offset upper
    slli t5, t3, 2       # offset lower
    add t6, a0, t4       # &vec[length/2]
    lw t4, 0(t6)         # vec[length/2]
    add t6, a0, t5       # &vec[length/2 -1]
    lw t5, 0(t6)         # vec[length/2 -1]
    add t4, t4, t5       # soma
    srai t4, t4, 1       # divide por 2
    sw t4, 0(a2)         # *me = mediana
    li a0, 1
    j end

invalid:
    li a0, 0             # retorna 0

end:
    lw ra, 12(sp)        # load do return adress
    addi sp, sp, 16
    ret