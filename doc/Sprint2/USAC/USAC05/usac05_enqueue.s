.section .text
.global enqueue_value
# int enqueue_value(int* buffer, int length, int* nelem, int* tail, int* head, int value)
# a0=buffer, a1=length, a2=&nelem, a3=&tail, a4=&head, a5=value, return a0: 1 if full after insert, else 0
enqueue_value:
    # load *nelem, *head, *tail
    lw   t0, 0(a2)          # t0 = *nelem
    lw   t1, 0(a4)          # t1 = *head
    lw   t2, 0(a3)          # t2 = *tail

    # if full: advance tail (discard oldest), keep *nelem
    bne  t0, a1, .L_not_full
    addi t2, t2, 1
    blt  t2, a1, .L_store_tail_full
    li   t2, 0
.L_store_tail_full:
    sw   t2, 0(a3)
    j    .L_write

.L_not_full:
    addi t0, t0, 1
    sw   t0, 0(a2)

.L_write:
    slli t3, t1, 2          # &buffer[head]
    add  t3, a0, t3
    sw   a5, 0(t3)          # store value

    addi t1, t1, 1          # advance head
    blt  t1, a1, .L_store_head
    li   t1, 0
.L_store_head:
    sw   t1, 0(a4)

    # return 1 if *nelem == length, else 0
    lw   t4, 0(a2)
    beq  t4, a1, .L_ret1
    li   a0, 0
    ret
.L_ret1:
    li   a0, 1
    ret
