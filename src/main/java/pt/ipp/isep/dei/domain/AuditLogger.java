package main.java.pt.ipp.isep.dei.domain;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe responsável por registar eventos ou ações num ficheiro de auditoria (log).
 *
 * <p>O {@code AuditLogger} permite criar registos cronológicos com timestamp
 * para acompanhar eventos relevantes, como operações do utilizador, falhas do sistema,
 * ou outras ações críticas.</p>
 *
 * <p>Cada linha do log contém um carimbo temporal no formato
 * {@code yyyy-MM-dd HH:mm}, seguido de uma mensagem descritiva.</p>
 */
public class AuditLogger {

    /** Caminho do ficheiro onde os logs serão guardados. */
    private final String filePath;

    /**
     * Constrói um {@code AuditLogger} que escreve no ficheiro especificado.
     * O ficheiro existente, caso exista, é preservado (modo append).
     *
     * @param filePath caminho completo para o ficheiro de log
     */
    public AuditLogger(String filePath) {
        this(filePath, false);
    }

    /**
     * Constrói um {@code AuditLogger} com a opção de sobrescrever o ficheiro existente.
     *
     * @param filePath  caminho completo para o ficheiro de log
     * @param overwrite se {@code true}, o ficheiro é limpo ao iniciar;
     *                  se {@code false}, os registos são acrescentados ao ficheiro existente
     */
    public AuditLogger(String filePath, boolean overwrite) {
        this.filePath = filePath;
        if (overwrite) clearFile();
    }

    /**
     * Limpa o conteúdo do ficheiro de log, caso exista.
     * Este método é chamado automaticamente apenas se a opção {@code overwrite}
     * for ativada no construtor.
     */
    private void clearFile() {
        try (FileWriter fw = new FileWriter(filePath, false)) {
            // apenas abre e fecha — apaga o conteúdo existente
        } catch (IOException e) {
            System.err.println("⚠️ Could not clear audit log: " + e.getMessage());
        }
    }

    /**
     * Regista uma nova entrada no ficheiro de log, com data e hora atuais.
     *
     * @param message mensagem a ser registada no log
     */
    public void log(String message) {
        try (FileWriter fw = new FileWriter(filePath, true)) {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            fw.write(timestamp + " | " + message + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("❌ Error writing to audit log: " + e.getMessage());
        }
    }
}
