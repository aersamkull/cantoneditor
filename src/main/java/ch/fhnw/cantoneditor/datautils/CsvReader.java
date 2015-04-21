package ch.fhnw.cantoneditor.datautils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import au.com.bytecode.opencsv.CSVReader;
import ch.fhnw.cantoneditor.model.Canton;

public class CsvReader {

    public static void readCantons() throws IOException {
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(
                CsvReader.class.getResourceAsStream("/cantons.csv"), "UTF-8"));
        try (CSVReader reader = new CSVReader(fileReader, ';', '"', 1)) {

            // Read CSV line by line and use the string array as you want
            String[] nextLine;
            ArrayList<Canton> cantons = new ArrayList<Canton>();
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine != null) {
                    Canton c = new Canton();
                    c.setName(nextLine[0]);
                    c.setShortCut(nextLine[1]);
                    c.setCantonNr(Integer.parseInt(nextLine[2].replace("'", "")));
                    c.setNrCouncilSeats((int) (Double.parseDouble(nextLine[3]) * 2.0));
                    c.setEntryYear(Integer.parseInt(nextLine[4].replace("'", "")));
                    c.setCapital(nextLine[5]);
                    c.setNrInhabitants(Integer.parseInt(nextLine[6].replace("'", "")));
                    c.setNrForeigners(Double.parseDouble(nextLine[7].replace("'", "")));
                    c.setArea(Double.parseDouble(nextLine[8].replace("'", "")));
                    c.setInHabitantDensity(Double.parseDouble(nextLine[9].replace("'", "")));
                    c.setNrCommunes(Integer.parseInt(nextLine[10].replace("'", "")));
                    // nextLine[11] is Languages

                    cantons.add(c);
                    // Verifying the read data here
                    System.out.println(Arrays.toString(nextLine));
                }
            }
        }
    }
}