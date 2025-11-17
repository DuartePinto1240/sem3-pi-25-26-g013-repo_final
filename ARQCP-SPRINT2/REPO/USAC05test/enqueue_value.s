    .section .text
    .global enqueue_value

# int enqueue_value(int* buffer, int length, int* nelem, int* rd, int* wr, int value)
# a0 = buffer
# a1 = length
# a2 = nelem pointer
# a3 = rd pointer   (read index / head)
# a4 = wr pointer   (write index / tail)
# a5 = value

enqueue_value:

    # Load *nelem, *rd (head), *wr (tail)
    lw   t0, 0(a2)          # t0 = *nelem
    lw   t1, 0(a3)          # t1 = *rd (head)
    lw   t2, 0(a4)          # t2 = *wr (tail)

    # If full: advance rd (discard oldest)
    bne  t0, a1, .not_full
    addi t1, t1, 1
    blt  t1, a1, .store_head_full
    li   t1, 0
.store_head_full:
    sw   t1, 0(a3)          # *rd = updated head
    j    .write_value

.not_full:
    addi t0, t0, 1
    sw   t0, 0(a2)          # *nelem = *nelem + 1

.write_value:
    # buffer[*wr] = value
    slli t3, t2, 2          # t3 = wr * 4
    add  t3, a0, t3
    sw   a5, 0(t3)

    # wr = (wr + 1) % length
    addi t2, t2, 1
    blt  t2, a1, .store_tail
    li   t2, 0
.store_tail:
    sw   t2, 0(a4)          # *wr = updated tail

    # return 1 if full (nelem == length), else 0
    lw   t4, 0(a2)
    beq  t4, a1, .ret1
    li   a0, 0
    ret

.ret1:
    li   a0, 1
    ret
