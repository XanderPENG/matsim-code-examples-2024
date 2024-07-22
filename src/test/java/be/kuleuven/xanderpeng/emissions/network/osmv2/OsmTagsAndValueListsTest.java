package be.kuleuven.xanderpeng.emissions.network.osmv2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.matsim.testcases.MatsimTestUtils;

import java.util.List;

public class OsmTagsAndValueListsTest {
    private final static Logger LOG = LogManager.getLogger(OsmTagsAndValueListsTest.class);


    @Test
    public void addTagValueLists() {
        String tag = "bicycle";
        List<String> valueList = List.of("yes", "lane");
        OsmTagsAndValueLists tagsAndValueLists = new OsmTagsAndValueLists.Builder().addTagAndValues(tag, valueList).build();
        Assertions.assertEquals(valueList, tagsAndValueLists.getTagsAndValueLists().get(tag), "Wrong value list");

        LOG.info("The tag is " + tag);
    }

}
