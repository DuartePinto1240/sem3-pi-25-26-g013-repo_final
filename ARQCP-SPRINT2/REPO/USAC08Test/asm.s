.section .text
.global sort_array

# int sort_array(int *vec, int length, char order)
sort_array:
    blez    a1, fail                # if (length <= 0) → return 0
    beqz    a2, sort_desc           # if (order == 0) jump to descending sort


# ASCENDING BUBBLE SORT
sort_asc:
    li      t0, 0                   # i = 0 (outer loop index)

loop1:
    li      t1, 0                   # j = 0 (inner loop index)
    bge     t0, a1, end             # if i >= length → done

loop2:
    # compute (length - i - 1)
    sub     t2, a1, t0
    li      t3, 1
    sub     t2, t2, t3              # t2 = length - i - 1

    bge     t1, t2, increment_i     # if j >= length-i-1 → next i

    # load vec[j] and vec[j+1]
    lw      t3, 0(a0)               # vec[j]
    lw      t4, 4(a0)               # vec[j+1]

    ble     t3, t4, increment_j     # already in order (asc), skip swap

swap_if_greater:
    sw      t4, 0(a0)               # vec[j] = vec[j+1]
    sw      t3, 4(a0)               # vec[j+1] = old vec[j]
    j       increment_j

increment_i:
    addi    t0, t0, 1               # i++

    li      t2, 4                   # sizeof(int)
    mul     t5, t1, t2              # t5 = j * 4

    sub     a0, a0, t5              # restore a0 pointer backwards

    j       loop1

increment_j:
    addi    t1, t1, 1               # j++

    addi    a0, a0, 4               # advance pointer to vec[j+1]

    j       loop2


# DESCENDING BUBBLE SORT
sort_desc:
    li      t0, 0                   # i = 0
loop3:
    li      t1, 0                   # j = 0
    bge     t0, a1, end             # if i >= length → done

loop4:
    # compute (length - i - 1)
    sub     t2, a1, t0
    li      t3, 1
    sub     t2, t2, t3

    bge     t1, t2, increment_i1    # if j >= limit → next i

    # load vec[j] and vec[j+1]
    lw      t3, 0(a0)
    lw      t4, 4(a0)

    bge     t3, t4, increment_j1    # descending: if vec[j] >= vec[j+1], OK

swap_if_less:
    sw      t4, 0(a0)               # swap values
    sw      t3, 4(a0)
    j       increment_j1

increment_i1:
    addi    t0, t0, 1               # i++

    li      t2, 4
    mul     t5, t1, t2              # t5 = j*4

    sub     a0, a0, t5              # restore pointer

    j       loop3

increment_j1:
    addi    t1, t1, 1               # j++

    addi    a0, a0, 4               # pointer++

    j       loop4


# SUCCESS
end:
    li      a0, 1                   # return 1
    ret


# FAILURE
fail:
    li      a0, 0                   # return 0
    ret
