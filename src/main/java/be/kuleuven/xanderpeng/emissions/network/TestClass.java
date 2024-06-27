package be.kuleuven.xanderpeng.emissions.network;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TestClass {
    public static void main(String[] args) {
        String outputPath = utils.networkOutputDir + "GreatLeuven.xml";
        boolean isPathExist = Files.exists(Paths.get(outputPath));
        System.out.println(isPathExist);
    }
}
