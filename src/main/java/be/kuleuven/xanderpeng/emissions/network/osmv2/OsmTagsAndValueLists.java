package be.kuleuven.xanderpeng.emissions.network.osmv2;

import java.util.List;
import java.util.Map;

/**
 * This class contains the pairs of OSM tags and list-like values that are used in the network creation process.

 */
public class OsmTagsAndValueLists {
    private final Map<String, List<String>> TagsAndValueLists;

    private OsmTagsAndValueLists(Builder builder) {
        this.TagsAndValueLists = builder.TagsAndValueLists;
    }

    public Map<String, List<String>> getTagsAndValueLists() {
        return TagsAndValueLists;
    }

    // Builder class
    public static class Builder {
        private Map<String, List<String>> TagsAndValueLists;

        // A method that sets the Map directly
        public Builder setTagsAndValueLists(Map<String, List<String>> tagsAndValueLists) {
            TagsAndValueLists = tagsAndValueLists;
            return this;
        }

        public OsmTagsAndValueLists build() {
            return new OsmTagsAndValueLists(this);
        }

        /*
        * This method adds/updates list-like values to a specific Tag in the map
         */
        public Builder addTagAndValues(String tag, List<String> valueList) {
            if (TagsAndValueLists.containsKey(tag)) {
                List<String> existingValues = TagsAndValueLists.get(tag);
                existingValues.addAll(valueList);
                // Drop duplicates
               List<String> updatedValues = existingValues.stream().distinct().toList();
                TagsAndValueLists.put(tag, updatedValues);
            } else {
                TagsAndValueLists.put(tag, valueList);
            }
            return this;
        }

        public Builder addTagAndValuePair(String tag, String value) {
            if (TagsAndValueLists.containsKey(tag)) {
                List<String> existingValues = TagsAndValueLists.get(tag);
                existingValues.add(value);
                // Drop duplicates
                List<String> updatedValues = existingValues.stream().distinct().toList();
                TagsAndValueLists.put(tag, updatedValues);
            } else {
                TagsAndValueLists.put(tag, List.of(value));
            }
            return this;
        }
    }
}