import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CsvLoader {

    public List<Item> loadItems(String filePath) {
        List<Item> items = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Pular cabeçalho
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                items.add(new Item(fields[0], fields[1], fields[2], fields[3],
                        Double.parseDouble(fields[4]), Double.parseDouble(fields[5])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<Bay> loadBays(String filePath) {
        List<Bay> bays = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Pular cabeçalho
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(";");
                bays.add(new Bay(fields[0], Integer.parseInt(fields[1]), Integer.parseInt(fields[2]),
                        Integer.parseInt(fields[3])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bays;
    }

    public List<Wagon> loadWagons(String filePath) {
        List<Wagon> wagons = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Pular cabeçalho
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                // Adicionar a lógica para associar caixas a um vagão
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wagons;
    }
}



