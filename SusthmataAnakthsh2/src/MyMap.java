import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

//Extend ths LInkedHashMap gia na eisagoume diikes mas leitourgies 
public class MyMap<K, V> extends LinkedHashMap<K, V> {

	// me thn getInvetnory boroume na zhtsoume to value(Inventory) pou exei sthn 8esh h Map mas 
	public V getCount(int i) {
		//ap8hkeuoume se ena proswrino entry to Key , Value ths 8eshs i 
		Map.Entry<K, V> entry = this.getEntry(i);
		// ean einai null denkaanoume tipota 
		if (entry == null)
			return null;
		// alliws epistrefoume to value tou entry autou
		return entry.getValue();
	}
	// perpiou idia diadikasia kai me gia getProduct, apla auth epistrefei to Key kai oxi to Value 
	public K getString(int i) {
		// check if negetive index provided
		Set<Map.Entry<K, V>> entries = entrySet();
		int j = 0;

		for (Map.Entry<K, V> entry : entries)
			if (j++ == i)
				return entry.getKey();

		return null;

	}
	// h sunarths pou xrhsimopoioun oi 2 apopanw me8odoi h opoia epistrefei to Key , Value pou exei h Map mas 
	// Sthn ousia kanei iterate thn Map 
	// Ari8mei mia 8esh ths , elegxei ean einai ish me to i kai sthn periptwsh pou einai epistrefei to stoixeio pisw 
	public Map.Entry<K, V> getEntry(int i) {
		// check if negative index provided
		Set<Map.Entry<K, V>> entries = entrySet();
		int j = 0;

		for (Map.Entry<K, V> entry : entries)
			if (j++ == i)
				return entry;

		return null;

	}



}
