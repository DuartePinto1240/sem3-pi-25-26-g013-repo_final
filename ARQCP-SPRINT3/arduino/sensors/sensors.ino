/*
 * Railway Station - Sensors Component
 * Raspberry Pi Pico W Version
 * Lê temperatura e humidade e responde a comandos GTH
 */

// O Pico W tem sensor de temperatura interno!
// Não precisa de DHT22 externo (mas podes usar se quiseres)

// Opção 1: Usar sensor interno do Pico W (apenas temperatura)
// Opção 2: Usar DHT22 externo (temperatura + humidade)

// Descomenta a linha abaixo se tiveres DHT22 externo
// #define USE_DHT22

#ifdef USE_DHT22
  #include <DHT.h>
  #define DHTPIN 15        // GP15 no Pico
  #define DHTTYPE DHT22
  DHT dht(DHTPIN, DHTTYPE);
#endif

String command = "";

// Variáveis para simular humidade (se não tiveres DHT22)
int simulatedHumidity = 60;

void setup() {
  // Inicializar comunicação serial USB
  Serial.begin(9600);

  // Aguardar conexão serial (opcional)
  while (!Serial && millis() < 3000) {
    delay(10);
  }

  #ifdef USE_DHT22
    dht.begin();
    Serial.println("Sensors Component - Raspberry Pi Pico W (DHT22)");
  #else
    Serial.println("Sensors Component - Raspberry Pi Pico W (Internal Sensor)");
  #endif

  Serial.println("Waiting for commands...");
}

void loop() {
  // Verificar se há dados disponíveis na serial
  while (Serial.available() > 0) {
    char c = Serial.read();

    if (c == '\n' || c == '\r') {
      if (command.length() > 0) {
        processCommand(command);
        command = "";
      }
    } else {
      command += c;
    }
  }

  delay(10);
}

void processCommand(String cmd) {
  cmd.trim();
  cmd.toUpperCase();

  if (cmd == "GTH") {
    sendSensorData();
  } else {
    Serial.println("ERROR: Unknown command");
  }
}

void sendSensorData() {
  float temperature;
  float humidity;

  #ifdef USE_DHT22
    // Usar DHT22 externo
    humidity = dht.readHumidity();
    temperature = dht.readTemperature();

    if (isnan(humidity) || isnan(temperature)) {
      Serial.println("ERROR: Failed to read from DHT sensor!");
      return;
    }

  #else
    // Usar sensor interno do Pico W
    // O Pico W tem um sensor de temperatura no chip RP2040
    temperature = analogReadTemp();

    // Simular humidade (o Pico não tem sensor de humidade interno)
    // Em produção, deverias ter um sensor externo
    simulatedHumidity += random(-2, 3); // Variar ±2%
    if (simulatedHumidity < 40) simulatedHumidity = 40;
    if (simulatedHumidity > 80) simulatedHumidity = 80;
    humidity = simulatedHumidity;
  #endif

  // Formatar resposta
  String response = "TEMP&unit:celsius&value:";
  response += String((int)temperature);
  response += "#HUM&unit:percentage&value:";
  response += String((int)humidity);

  Serial.println(response);
}

/*
 * NOTAS IMPORTANTES RASPBERRY PI PICO W:
 *
 * 1. Usar Arduino IDE com suporte para Pico:
 *    - Adicionar URL em File > Preferences > Additional Board Manager URLs:
 *      https://github.com/earlephilhower/arduino-pico/releases/download/global/package_rp2040_index.json
 *    - Instalar "Raspberry Pi Pico/RP2040" em Tools > Board > Boards Manager
 *
 * 2. Selecionar Board:
 *    - Tools > Board > Raspberry Pi RP2040 Boards > Raspberry Pi Pico W
 *
 * 3. Pinos GPIO do Pico:
 *    - Usar GPxx em vez de pin numbers
 *    - Exemplo: GP15 em vez de Pin 15
 *
 * 4. Sensor Interno:
 *    - analogReadTemp() lê temperatura do chip RP2040
 *    - Não tem sensor de humidade interno
 *    - Para humidade real, precisa DHT22 externo
 *
 * 5. USB Serial:
 *    - Serial = USB (não precisa de pins RX/TX)
 *    - Baudrate: 9600 (ou qualquer valor, USB é sempre full-speed)
 */