package Askhsh1;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class PrettyPrintingMap<K, V> {
    private Map<K, V> map;

    public PrettyPrintingMap(Map<K, V> map) {
        this.map = map;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Entry<K, V>> iter = map.entrySet().iterator();
        int rank =map.size() ; 
        while (iter.hasNext()) {
            Entry<K, V> entry = iter.next();
            sb.append("Element :\"");
            sb.append(entry.getKey());
            sb.append("\" Frequency :\"");
            sb.append(entry.getValue());
            sb.append("\" Rank :\"");
            sb.append(rank+"\"");
            sb.append('\n');
            rank--;
            if (iter.hasNext()) {
                sb.append(' ');
            }
            
        }
        return sb.toString();

    }
}