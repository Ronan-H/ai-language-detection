package ie.gmit.sw;

import java.util.List;
import java.util.Map;

public class NetworkSelection {
    private List<Selection> selections;
    private Map<String, Selection> selectionMap;

    public NetworkSelection() {
    }

    public void addSelection(String key, Selection selection) {
        selections.add(selection);
        selectionMap.put(key, selection);
    }
}
