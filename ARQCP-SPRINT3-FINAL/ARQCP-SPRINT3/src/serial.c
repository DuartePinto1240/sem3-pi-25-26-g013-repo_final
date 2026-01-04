#include "serial.h"
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <termios.h>
#include <sys/select.h>

// Inicializar porta serial
int serial_init(SerialPort *port, const char *device, int baudrate) {
    if (!port || !device) return 0;

    strncpy(port->port, device, sizeof(port->port) - 1);
    port->baudrate = baudrate;
    port->is_open = 0;

    // Abrir porta serial
    port->fd = open(device, O_RDWR | O_NOCTTY | O_SYNC);
    if (port->fd < 0) {
        fprintf(stderr, "Erro ao abrir porta %s: %s\n", device, strerror(errno));
        return 0;
    }

    // Configurar parâmetros da porta
    struct termios tty;
    if (tcgetattr(port->fd, &tty) != 0) {
        fprintf(stderr, "Erro ao obter atributos: %s\n", strerror(errno));
        close(port->fd);
        return 0;
    }

    // Configurar baudrate
    speed_t speed = B9600; // Default
    if (baudrate == 115200) speed = B115200;

    cfsetospeed(&tty, speed);
    cfsetispeed(&tty, speed);

    // 8N1 mode (8 bits, sem paridade, 1 stop bit)
    tty.c_cflag = (tty.c_cflag & ~CSIZE) | CS8;
    tty.c_iflag &= ~IGNBRK;
    tty.c_lflag = 0;
    tty.c_oflag = 0;
    tty.c_cc[VMIN]  = 0;
    tty.c_cc[VTIME] = 5; // timeout de 0.5 segundos

    tty.c_iflag &= ~(IXON | IXOFF | IXANY);
    tty.c_cflag |= (CLOCAL | CREAD);
    tty.c_cflag &= ~(PARENB | PARODD);
    tty.c_cflag &= ~CSTOPB;
    tty.c_cflag &= ~CRTSCTS;

    if (tcsetattr(port->fd, TCSANOW, &tty) != 0) {
        fprintf(stderr, "Erro ao configurar porta: %s\n", strerror(errno));
        close(port->fd);
        return 0;
    }

    // Limpar buffers
    tcflush(port->fd, TCIOFLUSH);

    // Aguardar estabilização
    usleep(100000); // 100ms

    port->is_open = 1;
    printf("Porta serial %s aberta com sucesso!\n", device);

    return 1;
}

// Fechar porta serial
void serial_close(SerialPort *port) {
    if (port && port->is_open) {
        close(port->fd);
        port->is_open = 0;
        printf("Porta serial fechada.\n");
    }
}

// Enviar comando
int serial_send_command(SerialPort *port, const char *cmd) {
    if (!port || !port->is_open || !cmd) return 0;

    // Adicionar \n ao comando
    char buffer[256];
    snprintf(buffer, sizeof(buffer), "%s\n", cmd);

    int len = strlen(buffer);
    int written = write(port->fd, buffer, len);

    if (written != len) {
        fprintf(stderr, "Erro ao enviar comando: %s\n", strerror(errno));
        return 0;
    }

    // Flush para garantir envio imediato
    tcdrain(port->fd);

    printf("[SERIAL TX] %s", buffer);
    return 1;
}

// Ler resposta com timeout
int serial_read_response(SerialPort *port, char *buffer, int max_len, int timeout_ms) {
    if (!port || !port->is_open || !buffer) return 0;

    memset(buffer, 0, max_len);

    fd_set readfds;
    struct timeval timeout;
    int total_read = 0;
    int attempts = 0;
    const int max_attempts = 10;

    while (total_read < max_len - 1 && attempts < max_attempts) {
        FD_ZERO(&readfds);
        FD_SET(port->fd, &readfds);

        timeout.tv_sec = timeout_ms / 1000;
        timeout.tv_usec = (timeout_ms % 1000) * 1000;

        int ret = select(port->fd + 1, &readfds, NULL, NULL, &timeout);

        if (ret < 0) {
            fprintf(stderr, "Erro no select: %s\n", strerror(errno));
            return 0;
        } else if (ret == 0) {
            // Timeout
            if (total_read > 0) break; // Já lemos algo
            attempts++;
            continue;
        }

        // Ler dados disponíveis
        int n = read(port->fd, buffer + total_read, max_len - total_read - 1);
        if (n < 0) {
            fprintf(stderr, "Erro ao ler: %s\n", strerror(errno));
            return 0;
        } else if (n > 0) {
            total_read += n;
            
            // Verificar se recebemos linha completa
            if (strchr(buffer, '\n')) break;
        }

        attempts++;
    }

    // Remover caracteres de controle
    for (int i = 0; i < total_read; i++) {
        if (buffer[i] == '\r' || buffer[i] == '\n') {
            buffer[i] = '\0';
            break;
        }
    }

    if (total_read > 0) {
        printf("[SERIAL RX] %s\n", buffer);
        return 1;
    }

    return 0;
}

// Enviar comando de luz (específico para LightSigns)
int serial_send_light_command(SerialPort *port, const char *cmd) {
    if (!serial_send_command(port, cmd)) {
        return 0;
    }

    // Aguardar um pouco para o Arduino processar
    usleep(50000); // 50ms

    return 1;
}

// Obter dados dos sensores
int serial_get_sensor_data(SerialPort *port, char *response, int max_len) {
    // Enviar comando GTH
    if (!serial_send_command(port, "GTH")) {
        return 0;
    }

    // Aguardar resposta (timeout de 2 segundos)
    if (!serial_read_response(port, response, max_len, 2000)) {
        fprintf(stderr, "Timeout ao aguardar resposta dos sensores!\n");
        return 0;
    }

    // Validar formato da resposta
    if (strstr(response, "TEMP") && strstr(response, "HUM")) {
        return 1;
    }

    fprintf(stderr, "Resposta inválida dos sensores: %s\n", response);
    return 0;
}