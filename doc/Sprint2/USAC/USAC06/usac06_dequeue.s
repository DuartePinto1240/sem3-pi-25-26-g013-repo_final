.section .text
.global dequeue_value
# int dequeue_value(int* buffer, int length, int* nelem, int* tail, int* head, int* value)
# a0=buffer, a1=length, a2=&nelem, a3=&tail, a4=&head, a5=&value, return a0: 1 on success, 0 if empty
dequeue_value:
    lw   t0, 0(a2)          # t0 = *nelem
    beq  t0, x0, .L_empty   # if empty -> ret 0, *value=0

    lw   t2, 0(a3)          # t2 = *tail
    slli t3, t2, 2          # &buffer[tail]
    add  t3, a0, t3
    lw   t4, 0(t3)          # load oldest
    sw   t4, 0(a5)          # *value = oldest

    addi t2, t2, 1          # advance tail
    blt  t2, a1, .L_store_tail
    li   t2, 0
.L_store_tail:
    sw   t2, 0(a3)

    addi t0, t0, -1         # --*nelem
    sw   t0, 0(a2)

    li   a0, 1
    ret

.L_empty:
    sw   x0, 0(a5)          # *value = 0
    li   a0, 0
    ret
