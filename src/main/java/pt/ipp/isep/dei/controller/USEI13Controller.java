package main.java.pt.ipp.isep.dei.controller;

import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.domain.Track;
import main.java.pt.ipp.isep.dei.dto.HubCentralityResultDTO;
import main.java.pt.ipp.isep.dei.service.Graph;
import main.java.pt.ipp.isep.dei.service.RailHubCentralityService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class USEI13Controller {

    private static final String REPO_NAME = "sem3-pi-25-26-g013-repo";
    private static final String EXPORT_FOLDER_NAME = "USEI13-Hubs";
    private static final String EXPORT_FILE = "USEI13_Hubs.csv";
    private final RailHubCentralityService service = new RailHubCentralityService();

    public void exportToCsv(List<HubCentralityResultDTO> ranking) throws IOException {
        if (ranking == null || ranking.isEmpty()) {
            throw new IllegalStateException("No USEI13 results to export.");
        }

        File dir = getExportDirectory();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Could not create export directory: " + dir.getAbsolutePath());
        }

        File file = new File(dir, EXPORT_FILE);

        try (FileWriter fw = new FileWriter(file)) {
            fw.write("stid,stname,degree,strength,betweenness,harmonic_closeness,hubscore\n");

            for (HubCentralityResultDTO r : ranking) {
                fw.write(String.format(Locale.US,
                        "%s,\"%s\",%d,%.2f,%.6f,%.6f,%.6f%n",
                        r.stid,
                        r.stname.replace("\"", "\"\""),
                        r.degree,
                        r.strength,
                        r.betweenness,
                        r.harmonicCloseness,
                        r.hubScore
                ));
            }
        }
    }

    public String getExportPath() {
        File file = new File(getExportDirectory(), EXPORT_FILE);
        return file.getPath();
    }

    private File getExportDirectory() {
        String cwd = System.getProperty("user.dir"); // diretório onde o programa está a correr
        File current = new File(cwd);

        // Se já estamos dentro do repo (cwd termina com o nome do repo), não duplicar
        if (current.getName().equals(REPO_NAME)) {
            return new File(current, EXPORT_FOLDER_NAME);
        }

        // Caso contrário, cria dentro da pasta do repo
        return new File(new File(current, REPO_NAME), EXPORT_FOLDER_NAME);
    }

    public List<HubCentralityResultDTO> computeHubRanking(Graph<Station, Track> g) {
        return service.computeHubRanking(g);
    }
}
