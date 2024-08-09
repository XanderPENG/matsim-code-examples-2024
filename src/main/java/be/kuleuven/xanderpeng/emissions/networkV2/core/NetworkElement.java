package be.kuleuven.xanderpeng.emissions.networkV2.core;

import org.matsim.api.core.v01.Identifiable;
import org.matsim.api.core.v01.Id;

public final class NetworkElement {
    public interface Element{
        String getType();

    }

    public static class Node implements Element{
        public String getType(){
            return "Node";
        }
    }

}
