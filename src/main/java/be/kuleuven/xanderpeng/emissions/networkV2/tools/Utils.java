package be.kuleuven.xanderpeng.emissions.networkV2.tools;

public class Utils {
    // Set the path to the input OSM.pbf file
    public static String aldiNetworkInput = "src/main/resources/be.kuleuven.xanderpeng.emissions/network/input/mapSample.pbf";
    public static String greatLeuvenNetworkInput = "src/main/resources/be.kuleuven.xanderpeng.emissions/network/input/GreatLeuven.pbf";

    // Set the path to the output network file
    public static String networkOutputDir = "src/main/resources/be.kuleuven.xanderpeng.emissions/network/output/";

    // Set the CRS of Leuven
    public static String outputCrs = "EPSG:31370";  // lambert 72

    public static String id2String(Object id){
        boolean isNumber = id instanceof Number;
        if (id instanceof String){
            return (String)id;
        }else if (isNumber){
            return String.valueOf(id);
        }else{
            throw new IllegalArgumentException("The id should be either a string or a number.");
        }
    }

}
