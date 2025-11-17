    .section .text
    .global dequeue_value

# int dequeue_value(int* buffer, int length, int* nelem, int* rd, int* wr, int* value)
# a0 = buffer
# a1 = length
# a2 = &nelem
# a3 = &rd   (read index / oldest)
# a4 = &wr   (write index)
# a5 = &value
# return: a0 = 1 (success), 0 (empty)

dequeue_value:

    # load nelem
    lw   t0, 0(a2)          # t0 = *nelem
    beq  t0, x0, .L_empty   # empty? -> return 0, *value=0

    # load rd (read index / head)
    lw   t1, 0(a3)          # t1 = *rd

    # load buffer[*rd]
    slli t2, t1, 2          # offset = rd * 4
    add  t2, a0, t2
    lw   t3, 0(t2)          # t3 = buffer[*rd]

    # store into *value
    sw   t3, 0(a5)

    # advance rd = (rd + 1) % length
    addi t1, t1, 1
    blt  t1, a1, .L_store_rd
    li   t1, 0

.L_store_rd:
    sw   t1, 0(a3)

    # decrement nelem
    addi t0, t0, -1
    sw   t0, 0(a2)

    # return 1
    li   a0, 1
    ret

.L_empty:
    # *value = 0
    sw   x0, 0(a5)
    li   a0, 0
    ret
