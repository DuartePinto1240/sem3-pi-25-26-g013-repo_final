#ifndef SERIAL_H
#define SERIAL_H

// Estrutura para porta serial
typedef struct {
    int fd;                    // File descriptor
    char port[50];            // Nome da porta (ex: /dev/ttyACM0)
    int baudrate;             // Velocidade (9600)
    int is_open;              // Estado da conexão
} SerialPort;

// Funções de inicialização
int serial_init(SerialPort *port, const char *device, int baudrate);
void serial_close(SerialPort *port);

// Funções de comunicação
int serial_send_command(SerialPort *port, const char *cmd);
int serial_read_response(SerialPort *port, char *buffer, int max_len, int timeout_ms);

// Funções de alto nível para o projeto
int serial_send_light_command(SerialPort *port, const char *cmd);
int serial_get_sensor_data(SerialPort *port, char *response, int max_len);

#endif // SERIAL_H