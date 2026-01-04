/*
 * USAC10 - Station Controller (Unified)
 * Hardware: Raspberry Pi Pico W
 * * Funcionalidades combinadas:
 * 1. LightSigns: Controla 4 LEDs (GP13, GP14, GP22, GP21)
 * 2. Sensors: Lê Temp/Hum (Interno ou DHT22 no GP15)
 */

// --- CONFIGURAÇÃO: SENSORES ---
// Descomenta a linha abaixo se tiveres um DHT22 real ligado
// #define USE_DHT22

#ifdef USE_DHT22
  #include <DHT.h>
  #define DHTPIN 15        // Pino do sensor DHT
  #define DHTTYPE DHT22
  DHT dht(DHTPIN, DHTTYPE);
#endif

// --- CONFIGURAÇÃO: HARDWARE LEDS ---
#define LED_1  13   // Lado Esquerdo Externo
#define LED_2  14   // Lado Esquerdo Interno
#define LED_3  22   // Lado Direito Externo
#define LED_4  21   // Lado Direito Interno

// Variáveis Globais
String inputBuffer = "";
bool blinking = false;
unsigned long lastBlinkTime = 0;
int simulatedHumidity = 60; // Para simulação caso não haja DHT

// --- SETUP ---
void setup() {
  Serial.begin(9600);

  // Configurar LEDs
  pinMode(LED_1, OUTPUT);
  pinMode(LED_2, OUTPUT);
  pinMode(LED_3, OUTPUT);
  pinMode(LED_4, OUTPUT);
  pinMode(LED_BUILTIN, OUTPUT); // LED da placa para debug

  // Estado inicial: tudo apagado
  turnOffAllLEDs();

  // Inicializar Sensor (se ativado)
  #ifdef USE_DHT22
    dht.begin();
  #endif

  // Sequência de teste inicial visual
  testStartupSequence();

  Serial.println(">>> Station Controller ONLINE <<<");
  Serial.println("Modes: LightSigns (4-LED) + Sensors (GTH)");
  Serial.println("Waiting for commands...");
}

// --- LOOP PRINCIPAL ---
void loop() {
  // 1. Ler Serial (Não bloqueante)
  while (Serial.available() > 0) {
    char c = Serial.read();
    if (c == '\n' || c == '\r') {
      if (inputBuffer.length() > 0) {
        processGlobalCommand(inputBuffer);
        inputBuffer = "";
      }
    } else {
      inputBuffer += c;
    }
  }

  // 2. Gerir Piscas (Non-blocking Blink)
  handleBlinking();

  // Pequeno delay para estabilidade do CPU
  delay(10);
}

// --- PROCESSADOR DE COMANDOS ---
void processGlobalCommand(String cmd) {
  cmd.trim();
  cmd.toUpperCase();

  // Roteamento de Comandos
  if (cmd == "GTH") {
    // Comando para Sensores
    sendSensorData();
  }
  else if (cmd.length() >= 2) {
    // Comandos para Luzes (GE, RE, YE, RB, etc.)
    processLightCommand(cmd);
  }
}

// ==========================================
// LÓGICA DOS SENSORES
// ==========================================
void sendSensorData() {
  float temperature = 0.0;
  float humidity = 0.0;

  #ifdef USE_DHT22
    // Leitura real do DHT22
    humidity = dht.readHumidity();
    temperature = dht.readTemperature();

    if (isnan(humidity) || isnan(temperature)) {
      Serial.println("ERROR: DHT read failed");
      return; // Sai da função para não enviar lixo
    }
  #else
    // Leitura do Sensor Interno do Pico (RP2040)
    // Nota: analogReadTemp() é específico do core RP2040 para Arduino
    temperature = analogReadTemp();

    // Simulação de humidade (visto que o Pico não mede humidade interna)
    simulatedHumidity += random(-2, 3);
    if (simulatedHumidity < 40) simulatedHumidity = 40;
    if (simulatedHumidity > 80) simulatedHumidity = 80;
    humidity = simulatedHumidity;
  #endif

  // Formato Rigoroso USAC10:
  // TEMP&unit:celsius&value:xx#HUM&unit:percentage&value:xx

  String response = "TEMP&unit:celsius&value:";
  response += String((int)temperature);
  response += "#HUM&unit:percentage&value:";
  response += String((int)humidity);

  Serial.println(response);
}

// ==========================================
// LÓGICA DAS LUZES (LIGHTSIGNS)
// ==========================================
void processLightCommand(String cmd) {
  String operation = cmd.substring(0, 2); // Pega nos primeiros 2 chars (GE, RE...)

  // Apagar sempre antes de mudar estado (regra de exclusividade)
  turnOffAllLEDs();

  if (operation == "GE") {
    // GREEN: LEDs Externos (1 e 3)
    digitalWrite(LED_1, HIGH);
    digitalWrite(LED_3, HIGH);
    // Serial.println("ACK: GREEN"); // Comentado para não poluir o output se não quiseres

  } else if (operation == "RE") {
    // RED: Todos Ligados
    digitalWrite(LED_1, HIGH);
    digitalWrite(LED_2, HIGH);
    digitalWrite(LED_3, HIGH);
    digitalWrite(LED_4, HIGH);

  } else if (operation == "YE") {
    // YELLOW: LEDs Internos (2 e 4)
    digitalWrite(LED_2, HIGH);
    digitalWrite(LED_4, HIGH);

  } else if (operation == "RB") {
    // RED BLINK: Todos piscam
    blinking = true;
    lastBlinkTime = millis();
    // Liga imediatamente para dar feedback visual instantâneo
    digitalWrite(LED_1, HIGH);
    digitalWrite(LED_2, HIGH);
    digitalWrite(LED_3, HIGH);
    digitalWrite(LED_4, HIGH);

  } else if (operation == "OF") {
    // OFF: Tudo apagado (já feito pelo turnOffAllLEDs no início)
  }
}

void turnOffAllLEDs() {
  blinking = false;
  digitalWrite(LED_1, LOW);
  digitalWrite(LED_2, LOW);
  digitalWrite(LED_3, LOW);
  digitalWrite(LED_4, LOW);
}

void handleBlinking() {
  if (blinking) {
    unsigned long currentTime = millis();
    // Blink a cada 500ms
    if (currentTime - lastBlinkTime >= 500) {
      lastBlinkTime = currentTime;

      // Inverter estado de todos os LEDs
      int newState = !digitalRead(LED_1);
      digitalWrite(LED_1, newState);
      digitalWrite(LED_2, newState);
      digitalWrite(LED_3, newState);
      digitalWrite(LED_4, newState);
    }
  }
}

void testStartupSequence() {
  // Piscar sequencial rápido no arranque
  int leds[] = {LED_1, LED_2, LED_4, LED_3}; // Sequência circular
  for(int i=0; i<4; i++) {
    digitalWrite(leds[i], HIGH);
    delay(100);
    digitalWrite(leds[i], LOW);
  }
  // Piscar LED interno
  digitalWrite(LED_BUILTIN, HIGH);
  delay(200);
  digitalWrite(LED_BUILTIN, LOW);
}