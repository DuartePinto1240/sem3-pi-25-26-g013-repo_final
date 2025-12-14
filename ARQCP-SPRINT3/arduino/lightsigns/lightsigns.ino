/*
 * Railway Station - LightSigns Component
 * Controla LEDs RGB para sinalizar estado das vias
 * Comandos: RE (Red), YE (Yellow), GE (Green), RB (Red Blink)
 */

// Definir número máximo de vias
#define MAX_TRACKS 5

// Estrutura para cada via
struct Track {
  int redPin;
  int greenPin;
  int bluePin;
  int state;      // 0=Green, 1=Red, 2=Yellow, 3=RedBlink
  bool blinking;
  unsigned long lastBlinkTime;
};

// Array de vias (via 1 a via 5)
Track tracks[MAX_TRACKS];

// Configuração dos pinos (ajustar conforme teu hardware)
void setupTrackPins() {
  // Via 1
  tracks[0].redPin = 2;
  tracks[0].greenPin = 3;
  tracks[0].bluePin = 4;

  // Via 2
  tracks[1].redPin = 5;
  tracks[1].greenPin = 6;
  tracks[1].bluePin = 7;

  // Via 3
  tracks[2].redPin = 8;
  tracks[2].greenPin = 9;
  tracks[2].bluePin = 10;

  // Via 4
  tracks[3].redPin = 11;
  tracks[3].greenPin = 12;
  tracks[3].bluePin = 13;

  // Via 5
  tracks[4].redPin = A0;
  tracks[4].greenPin = A1;
  tracks[4].bluePin = A2;

  // Configurar todos os pinos como OUTPUT
  for (int i = 0; i < MAX_TRACKS; i++) {
    pinMode(tracks[i].redPin, OUTPUT);
    pinMode(tracks[i].greenPin, OUTPUT);
    pinMode(tracks[i].bluePin, OUTPUT);

    // Inicializar estados
    tracks[i].state = 0;
    tracks[i].blinking = false;
    tracks[i].lastBlinkTime = 0;

    // Apagar todos os LEDs
    digitalWrite(tracks[i].redPin, LOW);
    digitalWrite(tracks[i].greenPin, LOW);
    digitalWrite(tracks[i].bluePin, LOW);
  }
}

String command = "";

void setup() {
  Serial.begin(9600);

  setupTrackPins();

  Serial.println("LightSigns Component Initialized");
  Serial.println("Waiting for commands...");
  Serial.println("Format: CMD,XX (e.g., GE,01 or RB,03)");
}

void loop() {
  // Processar comandos
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

  // Processar blink dos LEDs
  handleBlinking();

  delay(10);
}

void processCommand(String cmd) {
  cmd.trim();
  cmd.toUpperCase();

  // Formato esperado: CMD,XX
  // Exemplo: GE,01 (Green para via 1)

  if (cmd.length() < 4) {
    Serial.println("ERROR: Invalid command format");
    return;
  }

  String operation = cmd.substring(0, 2);
  int trackNum = cmd.substring(3).toInt();

  if (trackNum < 1 || trackNum > MAX_TRACKS) {
    Serial.println("ERROR: Invalid track number");
    return;
  }

  int trackIndex = trackNum - 1;

  // Apagar todos os LEDs da via primeiro
  turnOffTrack(trackIndex);

  if (operation == "RE") {
    // Red - Via ocupada
    setTrackRed(trackIndex);
    Serial.print("Track ");
    Serial.print(trackNum);
    Serial.println(" set to RED");

  } else if (operation == "YE") {
    // Yellow - Via atribuída
    setTrackYellow(trackIndex);
    Serial.print("Track ");
    Serial.print(trackNum);
    Serial.println(" set to YELLOW");

  } else if (operation == "GE") {
    // Green - Via livre
    setTrackGreen(trackIndex);
    Serial.print("Track ");
    Serial.print(trackNum);
    Serial.println(" set to GREEN");

  } else if (operation == "RB") {
    // Red Blink - Via não operacional
    setTrackRedBlink(trackIndex);
    Serial.print("Track ");
    Serial.print(trackNum);
    Serial.println(" set to RED BLINK");

  } else if (operation == "OF") {
    // Off - Apagar
    Serial.print("Track ");
    Serial.print(trackNum);
    Serial.println(" turned OFF");

  } else {
    Serial.println("ERROR: Unknown operation");
  }
}

void turnOffTrack(int index) {
  digitalWrite(tracks[index].redPin, LOW);
  digitalWrite(tracks[index].greenPin, LOW);
  digitalWrite(tracks[index].bluePin, LOW);
  tracks[index].blinking = false;
}

void setTrackRed(int index) {
  digitalWrite(tracks[index].redPin, HIGH);
  digitalWrite(tracks[index].greenPin, LOW);
  digitalWrite(tracks[index].bluePin, LOW);
  tracks[index].state = 1;
  tracks[index].blinking = false;
}

void setTrackGreen(int index) {
  digitalWrite(tracks[index].redPin, LOW);
  digitalWrite(tracks[index].greenPin, HIGH);
  digitalWrite(tracks[index].bluePin, LOW);
  tracks[index].state = 0;
  tracks[index].blinking = false;
}

void setTrackYellow(int index) {
  digitalWrite(tracks[index].redPin, HIGH);
  digitalWrite(tracks[index].greenPin, HIGH);
  digitalWrite(tracks[index].bluePin, LOW);
  tracks[index].state = 2;
  tracks[index].blinking = false;
}

void setTrackRedBlink(int index) {
  tracks[index].state = 3;
  tracks[index].blinking = true;
  tracks[index].lastBlinkTime = millis();
  digitalWrite(tracks[index].redPin, HIGH);
  digitalWrite(tracks[index].greenPin, LOW);
  digitalWrite(tracks[index].bluePin, LOW);
}

void handleBlinking() {
  unsigned long currentTime = millis();

  for (int i = 0; i < MAX_TRACKS; i++) {
    if (tracks[i].blinking) {
      // Blink a cada 500ms
      if (currentTime - tracks[i].lastBlinkTime >= 500) {
        tracks[i].lastBlinkTime = currentTime;

        // Toggle LED vermelho
        int currentState = digitalRead(tracks[i].redPin);
        digitalWrite(tracks[i].redPin, !currentState);
      }
    }
  }
}

/*
 * INSTRUÇÕES DE TESTE:
 *
 * 1. Carregar este código no Arduino
 * 2. Ligar LEDs RGB aos pinos definidos
 * 3. Abrir Serial Monitor (9600 baud)
 * 4. Enviar comandos:
 *    - GE,01  -> Via 1 verde (livre)
 *    - RE,02  -> Via 2 vermelho (ocupada)
 *    - YE,03  -> Via 3 amarelo (atribuída)
 *    - RB,04  -> Via 4 vermelho intermitente (não operacional)
 *
 * HARDWARE:
 * - Arduino Uno ou Raspberry Pi Pico
 * - LEDs RGB (ou LEDs separados)
 * - Resistores de 220Ω para cada LED
 *
 * LIGAÇÕES (para cada via):
 * - LED Red (Anodo) -> Resistor -> Pin digital
 * - LED Green (Anodo) -> Resistor -> Pin digital
 * - LED Blue (Anodo) -> Resistor -> Pin digital
 * - LED Catodo comum -> GND
 */