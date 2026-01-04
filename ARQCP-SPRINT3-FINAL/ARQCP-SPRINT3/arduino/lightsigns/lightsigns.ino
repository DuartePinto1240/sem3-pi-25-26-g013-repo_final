/*
 * Railway Station - LightSigns Component (4 LEDs)
 * Raspberry Pi Pico W Version
 *
 * Hardware: 4 LEDs nos pinos GP13, GP14, GP22, GP21
 * Layout: 2 LEDs de cada lado
 */

// Definir pinos dos LEDs (conforme o teu hardware)
#define LED_1  13   // GP13 - LED 1 (Lado Esquerdo)
#define LED_2  14   // GP14 - LED 2 (Lado Esquerdo)
#define LED_3  22   // GP22 - LED 3 (Lado Direito)
#define LED_4  21   // GP21 - LED 4 (Lado Direito)

// Variáveis para controlo de blink
bool blinking = false;
unsigned long lastBlinkTime = 0;
int currentState = 0;  // 0=Free, 1=Busy, 2=Assigned, 3=Inoperative

String command = "";

void setup() {
  Serial.begin(9600);

  // Aguardar conexão serial
  while (!Serial && millis() < 3000) {
    delay(10);
  }

  // Configurar pinos dos LEDs como OUTPUT
  pinMode(LED_1, OUTPUT);
  pinMode(LED_2, OUTPUT);
  pinMode(LED_3, OUTPUT);
  pinMode(LED_4, OUTPUT);
  pinMode(LED_BUILTIN, OUTPUT);

  // Apagar todos os LEDs
  digitalWrite(LED_1, LOW);
  digitalWrite(LED_2, LOW);
  digitalWrite(LED_3, LOW);
  digitalWrite(LED_4, LOW);

  // Teste inicial - acender LEDs em sequência
  testLEDs();

  // LED interno pisca 3x
  for (int i = 0; i < 3; i++) {
    digitalWrite(LED_BUILTIN, HIGH);
    delay(200);
    digitalWrite(LED_BUILTIN, LOW);
    delay(200);
  }

  Serial.println("╔═══════════════════════════════════════════════════════════╗");
  Serial.println("║  LightSigns Component - Raspberry Pi Pico W (4 LEDs)     ║");
  Serial.println("╚═══════════════════════════════════════════════════════════╝");
  Serial.println("");
  Serial.println("Hardware Configuration:");
  Serial.println("  Left Side:  LED 1 (GP13), LED 2 (GP14)");
  Serial.println("  Right Side: LED 3 (GP22), LED 4 (GP21)");
  Serial.println("");
  Serial.println("Available Commands:");
  Serial.println("  GE,01  - Green  (Via Livre) - LEDs 1,3 ON");
  Serial.println("  RE,01  - Red    (Via Ocupada) - LEDs 1,2,3,4 ON");
  Serial.println("  YE,01  - Yellow (Via Atribuída) - LEDs 2,4 ON");
  Serial.println("  RB,01  - Red Blink (Não Operacional) - ALL BLINK");
  Serial.println("  OF,01  - Off (Apagar tudo)");
  Serial.println("  TE     - Test all LEDs");
  Serial.println("");
  Serial.println("Waiting for commands...");
  Serial.println("─────────────────────────────────────────────────────────────");
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

  // Processar blink se estiver ativo
  handleBlinking();

  delay(10);
}

void testLEDs() {
  Serial.println("Testing LEDs in sequence...");

  // LED 1
  digitalWrite(LED_1, HIGH);
  Serial.println("  LED 1 (GP13) ON");
  delay(400);
  digitalWrite(LED_1, LOW);
  delay(100);

  // LED 2
  digitalWrite(LED_2, HIGH);
  Serial.println("  LED 2 (GP14) ON");
  delay(400);
  digitalWrite(LED_2, LOW);
  delay(100);

  // LED 3
  digitalWrite(LED_3, HIGH);
  Serial.println("  LED 3 (GP22) ON");
  delay(400);
  digitalWrite(LED_3, LOW);
  delay(100);

  // LED 4
  digitalWrite(LED_4, HIGH);
  Serial.println("  LED 4 (GP21) ON");
  delay(400);
  digitalWrite(LED_4, LOW);
  delay(100);

  // Todos juntos
  digitalWrite(LED_1, HIGH);
  digitalWrite(LED_2, HIGH);
  digitalWrite(LED_3, HIGH);
  digitalWrite(LED_4, HIGH);
  Serial.println("  ALL LEDs ON");
  delay(500);
  turnOffAllLEDs();
  delay(100);

  Serial.println("Test complete!");
}

void processCommand(String cmd) {
  cmd.trim();
  cmd.toUpperCase();

  if (cmd.length() < 2) {
    Serial.println("ERROR: Invalid command format");
    return;
  }

  // Extrair operação (pode ser GE, RE, YE, RB, OF, TE)
  String operation = cmd.substring(0, 2);

  // Apagar todos os LEDs primeiro
  turnOffAllLEDs();

  if (operation == "GE") {
    // Green - Via livre
    // Acender LEDs externos (1 e 3)
    setGreen();
    Serial.println("✓ Status: GREEN (Via Livre)");
    Serial.println("  LEDs ON: 1 (GP13), 3 (GP22)");

  } else if (operation == "RE") {
    // Red - Via ocupada
    // Acender todos os LEDs
    setRed();
    Serial.println("✓ Status: RED (Via Ocupada)");
    Serial.println("  LEDs ON: ALL (1,2,3,4)");

  } else if (operation == "YE") {
    // Yellow - Via atribuída
    // Acender LEDs internos (2 e 4)
    setYellow();
    Serial.println("✓ Status: YELLOW (Via Atribuída)");
    Serial.println("  LEDs ON: 2 (GP14), 4 (GP21)");

  } else if (operation == "RB") {
    // Red Blink - Via não operacional
    // Todos os LEDs piscam
    setRedBlink();
    Serial.println("✓ Status: RED BLINK (Via Não Operacional)");
    Serial.println("  LEDs BLINKING: ALL");

  } else if (operation == "OF") {
    // Off - Apagar
    Serial.println("✓ Status: OFF (Todos os LEDs apagados)");

  } else if (operation == "TE") {
    // Test - Testar LEDs
    Serial.println("✓ Running LED test...");
    testLEDs();

  } else if (operation == "L1") {
    // Teste individual - LED 1
    digitalWrite(LED_1, HIGH);
    Serial.println("✓ LED 1 (GP13) ON");

  } else if (operation == "L2") {
    // Teste individual - LED 2
    digitalWrite(LED_2, HIGH);
    Serial.println("✓ LED 2 (GP14) ON");

  } else if (operation == "L3") {
    // Teste individual - LED 3
    digitalWrite(LED_3, HIGH);
    Serial.println("✓ LED 3 (GP22) ON");

  } else if (operation == "L4") {
    // Teste individual - LED 4
    digitalWrite(LED_4, HIGH);
    Serial.println("✓ LED 4 (GP21) ON");

  } else if (operation == "AL") {
    // All LEDs ON
    digitalWrite(LED_1, HIGH);
    digitalWrite(LED_2, HIGH);
    digitalWrite(LED_3, HIGH);
    digitalWrite(LED_4, HIGH);
    Serial.println("✓ ALL LEDs ON");

  } else {
    Serial.println("ERROR: Unknown operation");
    Serial.println("Valid commands: GE, RE, YE, RB, OF, TE, L1, L2, L3, L4, AL");
    return;
  }

  // Piscar LED interno brevemente para confirmar
  digitalWrite(LED_BUILTIN, HIGH);
  delay(50);
  digitalWrite(LED_BUILTIN, LOW);

  Serial.println("─────────────────────────────────────────────────────────────");
}

void turnOffAllLEDs() {
  digitalWrite(LED_1, LOW);
  digitalWrite(LED_2, LOW);
  digitalWrite(LED_3, LOW);
  digitalWrite(LED_4, LOW);
  blinking = false;
}

void setGreen() {
  // Via livre - LEDs externos (1 e 3)
  digitalWrite(LED_1, HIGH);  // Esquerda externa
  digitalWrite(LED_2, LOW);
  digitalWrite(LED_3, HIGH);  // Direita externa
  digitalWrite(LED_4, LOW);
  currentState = 0;
  blinking = false;
}

void setRed() {
  // Via ocupada - Todos os LEDs
  digitalWrite(LED_1, HIGH);
  digitalWrite(LED_2, HIGH);
  digitalWrite(LED_3, HIGH);
  digitalWrite(LED_4, HIGH);
  currentState = 1;
  blinking = false;
}

void setYellow() {
  // Via atribuída - LEDs internos (2 e 4)
  digitalWrite(LED_1, LOW);
  digitalWrite(LED_2, HIGH);  // Esquerda interna
  digitalWrite(LED_3, LOW);
  digitalWrite(LED_4, HIGH);  // Direita interna
  currentState = 2;
  blinking = false;
}

void setRedBlink() {
  // Via não operacional - Todos piscam
  digitalWrite(LED_1, HIGH);
  digitalWrite(LED_2, HIGH);
  digitalWrite(LED_3, HIGH);
  digitalWrite(LED_4, HIGH);
  currentState = 3;
  blinking = true;
  lastBlinkTime = millis();
}

void handleBlinking() {
  if (blinking) {
    unsigned long currentTime = millis();

    // Blink a cada 500ms
    if (currentTime - lastBlinkTime >= 500) {
      lastBlinkTime = currentTime;

      // Toggle todos os LEDs
      int state = digitalRead(LED_1);
      digitalWrite(LED_1, !state);
      digitalWrite(LED_2, !state);
      digitalWrite(LED_3, !state);
      digitalWrite(LED_4, !state);
    }
  }
}

/*
 * ═══════════════════════════════════════════════════════════════
 * CONFIGURAÇÃO DO HARDWARE
 * ═══════════════════════════════════════════════════════════════
 *
 * Layout dos LEDs:
 *
 *      LADO ESQUERDO          LADO DIREITO
 *
 *         LED 1                  LED 3
 *         GP13                   GP22
 *          ●                      ●
 *                   VIA
 *          ●                      ●
 *         LED 2                  LED 4
 *         GP14                   GP21
 *
 * ═══════════════════════════════════════════════════════════════
 *
 * PADRÕES DE ILUMINAÇÃO:
 *
 * 🟢 GREEN (Via Livre):
 *    ●  ○     Externos acesos
 *
 *    ○  ●     (LEDs 1 e 3)
 *
 * 🔴 RED (Via Ocupada):
 *    ●  ●     Todos acesos
 *
 *    ●  ●     (LEDs 1, 2, 3, 4)
 *
 * 🟡 YELLOW (Via Atribuída):
 *    ○  ●     Internos acesos
 *
 *    ●  ○     (LEDs 2 e 4)
 *
 * 🔴 RED BLINK (Não Operacional):
 *    ●  ●     Todos piscam
 *
 *    ●  ●     (500ms ON/OFF)
 *
 * ═══════════════════════════════════════════════════════════════
 *
 * LIGAÇÕES (cada LED):
 *
 *   Raspberry Pi Pico W
 *   ┌──────────────┐
 *   │              │
 *   │   GP13 ○─────┼────→ [Resistor 220Ω] ──→ LED 1 (+) ──→ GND
 *   │   GP14 ○─────┼────→ [Resistor 220Ω] ──→ LED 2 (+) ──→ GND
 *   │   GP22 ○─────┼────→ [Resistor 220Ω] ──→ LED 3 (+) ──→ GND
 *   │   GP21 ○─────┼────→ [Resistor 220Ω] ──→ LED 4 (+) ──→ GND
 *   │   GND  ○─────┼────→ GND comum (todos os catodos)
 *   │              │
 *   └──────────────┘
 *
 * ═══════════════════════════════════════════════════════════════
 *
 * COMANDOS DISPONÍVEIS:
 *
 *   GE    → Green (Via Livre) - LEDs 1,3
 *   RE    → Red (Via Ocupada) - LEDs 1,2,3,4
 *   YE    → Yellow (Via Atribuída) - LEDs 2,4
 *   RB    → Red Blink (Não Operacional) - Todos piscam
 *   OF    → Off (Apagar tudo)
 *   TE    → Test (Testar todos em sequência)
 *
 *   Comandos de teste individual:
 *   L1    → Acender apenas LED 1 (GP13)
 *   L2    → Acender apenas LED 2 (GP14)
 *   L3    → Acender apenas LED 3 (GP22)
 *   L4    → Acender apenas LED 4 (GP21)
 *   AL    → Acender todos
 *
 * ═══════════════════════════════════════════════════════════════
 *
 * PINOUT RASPBERRY PI PICO W:
 *
 *                      USB
 *                  ┌────────┐
 *          GP0  ○──┤  1  40 ├──○  VBUS
 *          ...     │   ...  │     ...
 *         GP13  ○──┤ 17  24 ├──○  GP18
 *         GP14  ○──┤ 19  22 ├──○  GP17
 *          ...     │   ...  │     ...
 *         GP21  ○──┤ 27  15 ├──○  GP11
 *         GP22  ○──┤ 29  14 ├──○  GP10
 *          ...     │   ...  │     ...
 *                  └────────┘
 *
 *   Localização física dos pinos:
 *   - GP13: Pin 17 (lado esquerdo, meio)
 *   - GP14: Pin 19 (lado esquerdo, meio-baixo)
 *   - GP21: Pin 27 (lado direito, meio-baixo)
 *   - GP22: Pin 29 (lado direito, baixo)
 *
 * ═══════════════════════════════════════════════════════════════
 */