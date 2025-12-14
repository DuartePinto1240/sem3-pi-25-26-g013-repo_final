/*
 * Railway Station - Sensors Component
 * Lê temperatura e humidade do DHT22 e responde a comandos GTH
 */

#include <DHT.h>

// Pinos
#define DHTPIN 2        // Pino do sensor DHT22
#define DHTTYPE DHT22   // Tipo do sensor

// Criar objeto DHT
DHT dht(DHTPIN, DHTTYPE);

// Buffer para receber comandos
String command = "";

void setup() {
  // Inicializar comunicação serial
  Serial.begin(9600);

  // Inicializar sensor DHT
  dht.begin();

  // Mensagem de inicialização
  Serial.println("Sensors Component Initialized");
  Serial.println("Waiting for commands...");
}

void loop() {
  // Verificar se há dados disponíveis na serial
  while (Serial.available() > 0) {
    char c = Serial.read();

    if (c == '\n' || c == '\r') {
      // Processar comando quando receber newline
      if (command.length() > 0) {
        processCommand(command);
        command = "";
      }
    } else {
      command += c;
    }
  }

  delay(10); // Pequeno delay para não sobrecarregar
}

void processCommand(String cmd) {
  cmd.trim();
  cmd.toUpperCase();

  if (cmd == "GTH") {
    // Get Temperature and Humidity
    sendSensorData();
  } else {
    Serial.println("ERROR: Unknown command");
  }
}

void sendSensorData() {
  // Ler temperatura e humidade
  float humidity = dht.readHumidity();
  float temperature = dht.readTemperature();

  // Verificar se a leitura foi bem-sucedida
  if (isnan(humidity) || isnan(temperature)) {
    Serial.println("ERROR: Failed to read from DHT sensor!");
    return;
  }

  // Formatar resposta no formato esperado
  // TEMP&unit:celsius&value:xx#HUM&unit:percentage&value:xx
  String response = "TEMP&unit:celsius&value:";
  response += String((int)temperature);
  response += "#HUM&unit:percentage&value:";
  response += String((int)humidity);

  Serial.println(response);
}

// Função alternativa caso não tenhas sensor DHT22
// Envia valores simulados para teste
void sendSimulatedData() {
  // Gerar valores aleatórios para teste
  int temp = random(15, 30);   // Temperatura entre 15-30°C
  int hum = random(40, 80);    // Humidade entre 40-80%

  String response = "TEMP&unit:celsius&value:";
  response += String(temp);
  response += "#HUM&unit:percentage&value:";
  response += String(hum);

  Serial.println(response);
}

/*
 * INSTRUÇÕES DE TESTE:
 *
 * 1. Carregar este código no Arduino
 * 2. Abrir Serial Monitor (9600 baud)
 * 3. Enviar comando: GTH
 * 4. Deverá receber resposta: TEMP&unit:celsius&value:XX#HUM&unit:percentage&value:XX
 *
 * HARDWARE:
 * - Raspberry Pi Pico ou Arduino Uno
 * - Sensor DHT22 ligado ao pino 2
 * - DHT22 VCC -> 5V
 * - DHT22 GND -> GND
 * - DHT22 DATA -> Pin 2 (com resistor pull-up de 10kΩ)
 *
 * NOTA: Se não tiveres o sensor DHT22, substitui sendSensorData()
 * por sendSimulatedData() para testes
 */