# update_track_state.s
# Função assembly que recebe uma struct Track e atualiza o seu estado
#
# int update_track_state(Track *track, int new_state, int train_id)
#
# Struct Track (em C):
#   typedef struct {
#       int id;           // offset 0
#       int state;        // offset 4
#       int train_id;     // offset 8
#   } Track;
#
# Parâmetros:
#   a0 = ponteiro para Track
#   a1 = novo estado (0=Free, 1=Busy, 2=Assigned, 3=Inoperative)
#   a2 = train_id (-1 se não houver comboio)
#
# Retorno:
#   a0 = 1 se sucesso, 0 se erro
#
# Validações:
#   - Ponteiro não pode ser NULL
#   - Estado deve estar entre 0 e 3
#   - Se estado=Free (0), train_id deve ser -1
#   - Se estado=Busy/Assigned (1,2), train_id deve ser >= 0

.section .text
.globl update_track_state

update_track_state:
    # Verificar se ponteiro é NULL
    beqz a0, error              # if (track == NULL) return 0
    
    # Verificar se estado é válido (0-3)
    li t0, 0
    blt a1, t0, error           # if (new_state < 0) return 0
    li t0, 3
    bgt a1, t0, error           # if (new_state > 3) return 0
    
    # Verificar consistência: se Free, train_id deve ser -1
    li t0, 0                    # t0 = 0 (Free)
    bne a1, t0, check_busy      # if (new_state != Free) goto check_busy
    
    # Estado é Free, verificar train_id
    li t0, -1
    bne a2, t0, error           # if (train_id != -1) return 0
    j update_fields             # Validação OK, atualizar
    
check_busy:
    # Se estado é Busy ou Assigned (1 ou 2), train_id deve ser >= 0
    li t0, 1                    # t0 = 1 (Busy)
    beq a1, t0, check_train_valid
    
    li t0, 2                    # t0 = 2 (Assigned)
    beq a1, t0, check_train_valid
    
    # Estado é Inoperative (3), train_id deve ser -1
    li t0, -1
    bne a2, t0, error
    j update_fields
    
check_train_valid:
    # Para Busy/Assigned, train_id deve ser >= 0
    bltz a2, error              # if (train_id < 0) return 0
    
update_fields:
    # Atualizar campos da struct
    # track->state = new_state
    sw a1, 4(a0)                # Guardar new_state no offset 4
    
    # track->train_id = train_id
    sw a2, 8(a0)                # Guardar train_id no offset 8
    
    # Retornar sucesso
    li a0, 1                    # return 1
    ret
    
error:
    # Retornar erro
    li a0, 0                    # return 0
    ret


# Exemplo de uso em C:
#
# Track track;
# track.id = 1;
# track.state = 0;
# track.train_id = -1;
#
# // Atribuir via a comboio 101
# if (update_track_state(&track, 2, 101)) {  // 2 = Assigned
#     printf("Via atribuída com sucesso!\n");
#     printf("Estado: %d, Comboio: %d\n", track.state, track.train_id);
# }
#
# // Libertar via
# if (update_track_state(&track, 0, -1)) {   // 0 = Free
#     printf("Via libertada!\n");
# }
#
# // Erro: tentar atribuir Free com comboio
# if (!update_track_state(&track, 0, 101)) {
#     printf("Erro: Free não pode ter comboio!\n");
# }