.section .text
.global median
.extern sort_array

# int median(int* vec, int length, int *me)
# a0 = vec, a1 = length, a2 = me

median:

    addi sp, sp, -16
    sw ra, 12(sp)
    sw a0, 0(sp)   # guardar vec
    sw a1, 4(sp)   # guardar length
    sw a2, 8(sp)   # guardar me

    blez a1, invalid   # if (length <= 0) return 0

    li a2, 1
    call sort_array

    # restaurar parâmetros originais
    lw a0, 0(sp)
    lw a1, 4(sp)
    lw a2, 8(sp)

    # if (length % 2 == 1)
    rem t0, a1, 2
    beqz t0, even_case

odd_case:
    # *me = vec[length/2]
    div t1, a1, 2   # t1 = length / 2
    slli t1, t1, 2   # offset * 4
    add t2, a0, t1   # &vec[length/2]
    lw t3, 0(t2)   # vec[length/2]
    sw t3, 0(a2)   # *me = valor
    li a0, 1   # return 1
    j end

even_case:
     # *me = (vec[length/2 -1] + vec[length/2]) / 2
    div t1, a1, 2   # t1 = length / 2
    addi t2, t1, -1   #t2 = lenght/2 -1
    slli t1, t1, 2   # offset upper
    slli t2, t2, 2   # offset lower
    add t3, a0, t1    # &vec[length/2]
    add t4, a0, t2    # &vec[length/2 -1]
    lw t5, 0(t3)   # vec[length/2]
    lw t6, 0(t4)   # vec[length/2 -1]
    add t5, t5, t6   # soma
    srai t5, t5, 1   # divide por 2
    sw t5, 0(a2)   # *me = mediana
    li a0, 1
    j end

invalid:
    li a0, 0

end:
    lw ra, 12(sp)
    addi sp, sp, 16
    ret