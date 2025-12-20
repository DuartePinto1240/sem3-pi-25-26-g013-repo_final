/*
 * USAC10 - Station Controller (Unified)
 * Hardware: Raspberry Pi Pico W
 * * Funcionalidades Atualizadas (Sprint 3):
 * 1. LightSigns: Controla 6 LEDs (Tráfego Esquerdo e Direito)
 * 2. Sensors: Lê Temp/Hum (DHT22 no GP15 ou Simulado)
 */

// --- CONFIGURAÇÃO: SENSORES ---
// Descomenta a linha abaixo se tiveres um DHT22 real ligado
// #define USE_DHT22

#ifdef USE_DHT22
  #include <DHT.h>
  #define DHTPIN 15        // Pino do sensor DHT (Confirmado pelo user)
  #define DHTTYPE DHT22
  DHT dht(DHTPIN, DHTTYPE);
#endif

// --- CONFIGURAÇÃO: HARDWARE LEDS ---
// Lado Esquerdo
#define LED_L_RED    10
#define LED_L_YELLOW 11
#define LED_L_GREEN  12

// Lado Direito
#define LED_R_RED    20
#define LED_R_YELLOW 21
#define LED_R_GREEN  22

// Variáveis Globais
String inputBuffer = "";
bool blinking = false;
unsigned long lastBlinkTime = 0;
int simulatedHumidity = 60; // Para simulação caso não haja DHT

// --- SETUP ---
void setup() {
  Serial.begin(9600);

  // Configurar LEDs (Lado Esquerdo)
  pinMode(LED_L_RED, OUTPUT);
  pinMode(LED_L_YELLOW, OUTPUT);
  pinMode(LED_L_GREEN, OUTPUT);

  // Configurar LEDs (Lado Direito)
  pinMode(LED_R_RED, OUTPUT);
  pinMode(LED_R_YELLOW, OUTPUT);
  pinMode(LED_R_GREEN, OUTPUT);

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
  Serial.println("Modes: LightSigns (6-LED Traffic) + Sensors (GTH)");
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
  String operation = cmd.substring(0, 2); // Pega nos primeiros 2 chars

  // Apagar sempre antes de mudar estado (regra de exclusividade)
  turnOffAllLEDs();

  if (operation == "GE") {
    // GREEN: Sinal Verde (Via Livre)
    digitalWrite(LED_L_GREEN, HIGH);
    digitalWrite(LED_R_GREEN, HIGH);

  } else if (operation == "RE") {
    // RED: Sinal Vermelho (Via Ocupada)
    digitalWrite(LED_L_RED, HIGH);
    digitalWrite(LED_R_RED, HIGH);

  } else if (operation == "YE") {
    // YELLOW: Sinal Amarelo (Via Atribuída)
    digitalWrite(LED_L_YELLOW, HIGH);
    digitalWrite(LED_R_YELLOW, HIGH);

  } else if (operation == "RB") {
    // RED BLINK: Vermelho Intermitente (Não Operacional)
    blinking = true;
    lastBlinkTime = millis();
    // Liga imediatamente para dar feedback visual instantâneo
    digitalWrite(LED_L_RED, HIGH);
    digitalWrite(LED_R_RED, HIGH);

  } else if (operation == "OF") {
    // OFF: Tudo apagado (já feito pelo turnOffAllLEDs no início)
  }
}

void turnOffAllLEDs() {
  blinking = false;
  // Lado Esquerdo
  digitalWrite(LED_L_RED, LOW);
  digitalWrite(LED_L_YELLOW, LOW);
  digitalWrite(LED_L_GREEN, LOW);

  // Lado Direito
  digitalWrite(LED_R_RED, LOW);
  digitalWrite(LED_R_YELLOW, LOW);
  digitalWrite(LED_R_GREEN, LOW);
}

void handleBlinking() {
  if (blinking) {
    unsigned long currentTime = millis();
    // Blink a cada 500ms
    if (currentTime - lastBlinkTime >= 500) {
      lastBlinkTime = currentTime;

      // Inverter estado dos LEDs Vermelhos (para RB)
      int newStateLeft = !digitalRead(LED_L_RED);
      int newStateRight = !digitalRead(LED_R_RED);

      digitalWrite(LED_L_RED, newStateLeft);
      digitalWrite(LED_R_RED, newStateRight);
    }
  }
}

void testStartupSequence() {
  // Sequência Semafórica de Arranque (Vermelho -> Amarelo -> Verde)

  // 1. Vermelho
  digitalWrite(LED_L_RED, HIGH); digitalWrite(LED_R_RED, HIGH);
  delay(300);
  digitalWrite(LED_L_RED, LOW); digitalWrite(LED_R_RED, LOW);

  // 2. Amarelo
  digitalWrite(LED_L_YELLOW, HIGH); digitalWrite(LED_R_YELLOW, HIGH);
  delay(300);
  digitalWrite(LED_L_YELLOW, LOW); digitalWrite(LED_R_YELLOW, LOW);

  // 3. Verde
  digitalWrite(LED_L_GREEN, HIGH); digitalWrite(LED_R_GREEN, HIGH);
  delay(300);
  digitalWrite(LED_L_GREEN, LOW); digitalWrite(LED_R_GREEN, LOW);

  // Piscar LED interno
  digitalWrite(LED_BUILTIN, HIGH);
  delay(200);
  digitalWrite(LED_BUILTIN, LOW);
}