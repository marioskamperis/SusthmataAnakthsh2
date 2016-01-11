import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.stream.Stream;

public class Erwthma2Run {


	private static int NumberOfWords = 0;
	public static void main(String args[]) {
		long a = System.currentTimeMillis();
		//
		// HashMap map = (HashMap) tokenize("cran.qry");
		// //System.out.println("UNSORTED MAP
		// =====================================================\n"+new
		// PrettyPrintingMap<String, Integer>(map));
		// LinkedHashMap sorted_map = sortHashMapByValuesD(map);
		//
		// System.out.println("SORTED MAP
		// =====================================================\n" + new
		// PrettyPrintingMap<String, Integer>(sorted_map));
		//
		// System.out.println("Number of words " + N);
		//

		Map<String, Integer> map = (MyMap) tokenize("cran.all.1400");
		ValueComparator bvc = new ValueComparator(map);
		TreeMap temp_map = new TreeMap(bvc);
		Map<String, Integer> sorted_map = sortByValue(map);
		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream("ZipfStats.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.setOut(out);
		System.out.println("SORTED MAP \n" + new PrettyPrintingMap<String, Integer>(sorted_map));
		System.out.println("Sunepw parathroume oti o kanonas tou zipf isxuei sta dika ams text arxeia.");

		// List<MyPoint> list = new ArrayList<MyPoint>();
//		int rank = 1;
//		for (java.util.Map.Entry<String, Integer> entry : sorted_map.entrySet()) {
//
//			/// if (entry.getValue() > 2) {
//
//			 System.out.println("Frequency of Element :" + entry.getKey() + " = " + entry.getValue() + "\t\tand rank" + (rank++));
//
//			// list.add(new MyPoint(Math.log(entry.getValue()),
//			// Math.log(rank)));
//
//			// }
//		}

		System.out.println("Number of words " + NumberOfWords);
		long b = System.currentTimeMillis();
		System.out.println("Execution Time :" + (long) (b - a));

	}


	public static Map<String, Integer> tokenize(String path) {
		Map<String, Integer> numbers = new MyMap();
		StringTokenizer st = null;
		try {
			st = new StringTokenizer(readFile(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// analoga pws doueleuei h zipf ftiaxnoume logika mia map, arraylist
		// ,array[]
		while (st.hasMoreTokens()) {
			// get each word
			String word = st.nextToken();
			// strip the word from anything other than alphabetical characters
			word = word.replaceAll("[^a-zA-Z]", " ").trim();
			// if the map contains the word then find it and increase
			if (word.equals("") || word.equals(" ")) {
				continue;
			}
			NumberOfWords++;
			if (numbers.containsKey(word)) {
				for (java.util.Map.Entry<String, Integer> entry : numbers.entrySet()) {
					if (entry.getKey().equals(word)) {
						entry.setValue(entry.getValue() + 1);
					}
				}
			} else {
				numbers.put(word, new Integer(1));
			}
		}
		return numbers;
	}

	static String readFile(String path) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded);
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		Map<K, V> result = new LinkedHashMap<>();
		Stream<Entry<K, V>> st = map.entrySet().stream();

		st.sorted(Comparator.comparing(e -> e.getValue())).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));

		return result;
	}

}

class ValueComparator implements Comparator {
	Map base;

	public ValueComparator(Map base) {
		this.base = base;
	}

	@Override
	public int compare(Object arg0, Object arg1) {
		System.out.println("Arg0= " + arg0 + " Arg1= " + arg1);
		if ((Integer) base.get(arg0) >= (Integer) base.get(arg1)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}

