    .text
    .global decrypt_data

# int decrypt_data(char* in, int key, char* out)
# a0 = in
# a1 = key
# a2 = out
decrypt_data:
    addi sp, sp, -16        # allocate stack space
    sw ra, 12(sp)           # save return address
    sw s0, 8(sp)            # save s0
    sw s1, 4(sp)            # save s1
    sw s2, 0(sp)            # save s2

    mv s0, a0               # s0 = in pointer
    mv s1, a1               # s1 = key
    mv s2, a2               # s2 = out pointer

loop:
    lb t0, 0(s0)            # load byte from input
    beq t0, x0, finish_ok   # if '\0', end and return success

    # Check valid capital letter [A-Z]
    li t1, 'A'
    blt t0, t1, error       # if < 'A', error
    li t1, 'Z'
    bgt t0, t1, error       # if > 'Z', error

    # Decrypt Caesar cipher:
    # decrypted = (t0 - 'A' - key + 26) % 26 + 'A'

    addi t0, t0, -65        # t0 = char - 'A'
    sub  t0, t0, s1         # t0 = t0 - key
    addi t0, t0, 26         # t0 = t0 + 26
    li t1, 26
    rem  t0, t0, t1         # t0 = t0 % 26
    addi t0, t0, 65         # t0 = t0 + 'A'

    sb t0, 0(s2)            # store decrypted char

    addi s0, s0, 1          # move to next char input
    addi s2, s2, 1          # move to next char output
    j loop

finish_ok:
    sb x0, 0(s2)            # null-terminate output
    li a0, 1                # return 1 (success)
    j end

error:
    sb x0, 0(s2)            # set output as empty string
    li a0, 0                # return 0 (error)

end:
    lw ra, 12(sp)
    lw s0, 8(sp)
    lw s1, 4(sp)
    lw s2, 0(sp)
    addi sp, sp, 16
    ret
