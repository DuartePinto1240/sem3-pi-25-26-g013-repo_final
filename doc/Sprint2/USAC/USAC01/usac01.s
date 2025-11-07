.text
.globl encrypt_data

# int encrypt_data(char* in, int key, char *out)
# a0 = input string pointer
# a1 = key (1-26)
# a2 = output string pointer
# Returns: a0 = 1 (success) or 0 (failure)

encrypt_data:
    # Save registers
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
    blt a1, t0, error_exit      # if key < 1, error
    li t0, 26
    bgt a1, t0, error_exit      # if key > 26, error

    # Save pointers and key
    mv s0, a0                   # s0 = input pointer
    mv s1, a2                   # s1 = output pointer
    mv s2, a1                   # s2 = key

encrypt_loop:
    # Load current character
    lb t1, 0(s0)                # t1 = current char from input

    # Check if end of string (null terminator)
    beqz t1, encrypt_success

    # Validate character is uppercase letter (A-Z)
    li t2, 'A'                  # t2 = 'A' (65)
    blt t1, t2, error_exit      # if char < 'A', error
    li t2, 'Z'                  # t2 = 'Z' (90)
    bgt t1, t2, error_exit      # if char > 'Z', error

    # Apply Caesar cipher: ((char - 'A' + key) % 26) + 'A'
    li t2, 'A'
    sub t1, t1, t2              # t1 = char - 'A' (0-25)
    add t1, t1, s2              # t1 = (char - 'A') + key

    # Handle wrap-around using modulo 26
    li t3, 26
    rem t1, t1, t3              # t1 = (char - 'A' + key) % 26

    # Handle negative remainder (shouldn't happen with key in [1,26], but for safety)
    bge t1, zero, skip_adjust
    add t1, t1, t3              # if negative, add 26
skip_adjust:

    # Convert back to ASCII
    li t2, 'A'
    add t1, t1, t2              # t1 = result + 'A'

    # Store encrypted character
    sb t1, 0(s1)                # store to output

    # Move to next character
    addi s0, s0, 1              # increment input pointer
    addi s1, s1, 1              # increment output pointer

    j encrypt_loop              # continue loop

encrypt_success:
    # Add null terminator to output string
    sb zero, 0(s1)

    # Return 1 (success)
    li a0, 1

    # Restore registers and return
    lw s2, 0(sp)
    lw s1, 4(sp)
    lw s0, 8(sp)
    lw ra, 12(sp)
    addi sp, sp, 16
    ret

error_exit:
    # Set output string as empty (null terminator at start)
    beqz a2, skip_null          # if out pointer is NULL, skip
    sb zero, 0(a2)              # out[0] = '\0'
skip_null:

    # Return 0 (failure)
    li a0, 0

    # Restore registers and return
    lw s2, 0(sp)
    lw s1, 4(sp)
    lw s0, 8(sp)
    lw ra, 12(sp)
    addi sp, sp, 16
    ret