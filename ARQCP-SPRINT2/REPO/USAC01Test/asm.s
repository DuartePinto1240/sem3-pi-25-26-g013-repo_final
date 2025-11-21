.text
    .global encrypt_data

# int encrypt_data(char* in, int key, char *out)
# a0 = input string pointer
# a1 = key (1-26)
# a2 = output string pointer
# Returns: a0 = 1 (success) or 0 (failure)

encrypt_data:
    # Save registers (16 bytes stack frame)
    addi sp, sp, -16
    sw ra, 12(sp)
    sw s0, 8(sp)
    sw s1, 4(sp)
    sw s2, 0(sp)

    # Check if input pointer is NULL
    beqz a0, error_exit

    # Check if output pointer is NULL
    beqz a2, error_exit

    # Validate key range [1, 26]
    li t0, 1
    blt a1, t0, error_exit       # key < 1 → error
    li t0, 26
    bgt a1, t0, error_exit       # key > 26 → error

    # Save arguments in callee-saved registers
    mv s0, a0                    # s0 = in
    mv s1, a2                    # s1 = out
    mv s2, a1                    # s2 = key

encrypt_loop:
    # Load current character (unsigned!)
    lbu t1, 0(s0)

    # End of string?
    beqz t1, encrypt_success

    # Validate A–Z
    li t2, 'A'
    blt t1, t2, error_exit
    li t2, 'Z'
    bgt t1, t2, error_exit

    # Caesar Cipher: ((char - 'A' + key) % 26) + 'A'
    li t2, 'A'
    sub t1, t1, t2               # t1 = char - 'A'
    add t1, t1, s2               # + key

    li t3, 26
    rem t1, t1, t3               # modulo 26

    # Safety: adjust if modulo is negative (rare)
    bge t1, zero, skip_adjust
    add t1, t1, t3
skip_adjust:

    li t2, 'A'
    add t1, t1, t2               # back to ASCII

    # Store encrypted char
    sb t1, 0(s1)

    # Advance pointers
    addi s0, s0, 1
    addi s1, s1, 1
    j encrypt_loop

encrypt_success:
    # Null terminator
    sb zero, 0(s1)

    li a0, 1                     # return success
    j restore

error_exit:
    # out = "" (if not NULL)
    beqz a2, skip_null
    sb zero, 0(a2)
skip_null:
    li a0, 0                     # return failure

restore:
    # Restore registers
    lw s2, 0(sp)
    lw s1, 4(sp)
    lw s0, 8(sp)
    lw ra, 12(sp)
    addi sp, sp, 16
    ret


