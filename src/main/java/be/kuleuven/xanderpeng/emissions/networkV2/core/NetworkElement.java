package be.kuleuven.xanderpeng.emissions.networkV2.core;


import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Identifiable;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.network.Link;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Interim Network Elements, which can be easily converted to MATSim network elements;
 * Also, All input network-related data (e.g., OSM data, network with the shp format)
 * should be converted to these elements first.
 *
 * @author Xander
 */

public final class NetworkElement {

    public interface Element{
        String getType();
        Id<?> getId();
    }

    public static class Node implements Element{

        private final Id<Node> id;
        private final Coord coord;
        private final Map<Id<Link>, Link> relatedLinks = new HashMap<>(); // links that are connected to this node


        /*
        * 2 different constructors for different types of coordinate and id,
        * e.g., the id can be either a string or a long
         */
        public <T> Node(T id, Coord coord){
            this.id = Id.create((String)id, Node.class);
            this.coord = coord;
        }

        public <T> Node(T id, double...coords){
            this.id = Id.create((String)id, Node.class);
            if(coords.length == 2){
                this.coord = new Coord(coords[0], coords[1]);
            }else if(coords.length == 3){
                this.coord = new Coord(coords[0], coords[1], coords[2]);
            }else{
                throw new IllegalArgumentException("The number of coordinates should be either 2 or 3.");
            }
        }

        public String getType(){
            return "Node";
        }

        public Id<Node> getId(){
            return id;
        }

        public Coord getCoord(){
            return coord;
        }

        public Map<Id<Link>, Link> getRelatedLinks(){
            return relatedLinks;
        }

        public void addRelatedLink(Link link){
            relatedLinks.put(link.getId(), link);
        }

        public void addRelatedLinks(Set<Link> links){
            for(Link link : links){
                relatedLinks.put(link.getId(), link);
            }
        }

        @Override
        public boolean equals(Object obj){
            if(obj == this){
                return true;
            }
            if(obj == null || obj.getClass() != this.getClass()){
                return false;
            }

            Node node = (Node)obj;
            return node.getId().equals(this.getId());
        }

        @Override
        public int hashCode(){
            return this.getId().hashCode();
        }
    }


    public static class Link implements Element{

        private final Id<Link> id;
        private final Node fromNode;
        private final Node toNode;
        private final Map<Id<Node>, Node> composedNodes = new HashMap<>(); // nodes that are composed in this link
        private final Set<String> allowedModes = new HashSet<>(); // allowed modes for this link

        public <T>Link(T id, Node fromNode, Node toNode){
            this.id = Id.create((String)id, Link.class);
            this.fromNode = fromNode;
            this.toNode = toNode;
        }

        public String getType(){
            return "Link";
        }

        public Id<Link> getId(){
            return id;
        }

        public Node getFromNode(){
            return fromNode;
        }

        public Node getToNode(){
            return toNode;
        }

        public Map<Id<Node>, Node> getComposedNodes(){
            return composedNodes;
        }

        public Set<String> getAllowedModes(){
            return allowedModes;
        }

        public void addComposedNode(Node node){
            composedNodes.put(node.getId(), node);
        }

        public void addComposedNodes(Set<Node> nodes){
            for(Node node : nodes){
                composedNodes.put(node.getId(), node);
            }
        }

        public void addAllowedMode(String mode){
            allowedModes.add(mode);
        }

        public void addAllowedModes(Set<String> modes){
            allowedModes.addAll(modes);
        }

        @Override
        public boolean equals(Object obj){
            if(obj == this){
                return true;
            }
            if(obj == null || obj.getClass() != this.getClass()){
                return false;
            }

            Link link = (Link)obj;
            return link.getId().equals(this.getId());
        }

        @Override
        public int hashCode(){
            return this.getId().hashCode();
        }
    }

}
