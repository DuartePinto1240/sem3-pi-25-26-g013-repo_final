.section .text
.global sort_array

sort_array:
    beqz a2, sort_desc
sort_asc:
    li t0, 0 # i

loop1:
    li t1, 0 # j
    bge t0, a1, end

loop2:
    sub t2, a1, t0
    li t3, 1
    sub t2, t2, t3
    bge t1, t2, increment_i
    lw t3, 0(a0)
    lw t4, 4(a0)
    ble t3, t4, increment_j

swap_if_greater:
    sw t4, 0(a0)
    sw t3, 4(a0)
    j increment_j

increment_i:
    addi t0, t0, 1

    li t2, 4
    mul t5, t1, t2

    sub a0, a0, t5

    j loop1

increment_j:
    addi t1, t1, 1

    addi a0, a0, 4

    j loop2


sort_desc:
    li t0, 0 # i
loop3:
    li t1, 0 # j
    bge t0, a1, end

loop4:
    sub t2, a1, t0
    li t3, 1
    sub t2, t2, t3
    bge t1, t2, increment_i1
    lw t3, 0(a0)
    lw t4, 4(a0)
    bge t3, t4, increment_j1

swap_if_less:
    sw t4, 0(a0)
    sw t3, 4(a0)
    j increment_j1

increment_i1:
    addi t0, t0, 1

    li t2, 4
    mul t5, t1, t2

    sub a0, a0, t5

    j loop3

increment_j1:
    addi t1, t1, 1

    addi a0, a0, 4

    j loop4

end:
    ret