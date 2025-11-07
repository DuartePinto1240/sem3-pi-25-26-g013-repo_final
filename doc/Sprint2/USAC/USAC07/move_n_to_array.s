.section .text
.global move_n_to_array
.align 2

# int move_n_to_array(int *buffer, int length, int *nelem, int *tail, int *head, int n, int *array)
# Register mapping (ILP32D):
# a0=buffer, a1=length, a2=nelem, a3=tail, a4=head, a5=n, a6=array

move_n_to_array:
    blez    a5, ret0            # if (n <= 0) -> fail
    lw      t0, 0(a2)           # t0 = *nelem
    blt     t0, a5, ret0        # if (*nelem < n) -> fail
    blez    a1, ret0            # if (length <= 0) -> fail

    mv      t1, zero            # i = 0
    lw      t2, 0(a3)           # t2 = *tail

loop:
    bge     t1, a5, done_loop   # if (i >= n) break

    # load from buffer[*tail]
    slli    t3, t2, 2
    add     t4, a0, t3
    lw      t5, 0(t4)

    # store to array[i]
    slli    t6, t1, 2
    add     t3, a6, t6
    sw      t5, 0(t3)

    # advance tail
    addi    t2, t2, 1
    rem     t2, t2, a1

    # increment i
    addi    t1, t1, 1
    j       loop

done_loop:
    sw      t2, 0(a3)           # *tail = t2

    lw      t0, 0(a2)           # t0 = *nelem
    sub     t0, t0, a5          # t0 = *nelem - n
    sw      t0, 0(a2)           # *nelem = t0

    li      a0, 1
    ret

ret0:
    li      a0, 0
    ret