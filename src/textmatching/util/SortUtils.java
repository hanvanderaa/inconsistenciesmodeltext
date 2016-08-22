package textmatching.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import textmatching.alignment.Match;
import textmatching.inconsistencycheck.Prediction;
import textmatching.processmodel.Activity;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.Tree;

public class SortUtils {

	
	
	public static <K extends Comparable,V extends Comparable> Map<K,V> sortByValues(Map<K,V> map){
        List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());
      
        Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {

            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
      
        //LinkedHashMap will keep the keys in the order they are inserted
        //which is currently sorted on natural ordering
        Map<K,V> sortedMap = new LinkedHashMap<K,V>();
      
        for(Map.Entry<K,V> entry: entries){
            sortedMap.put(entry.getKey(), entry.getValue());
        }
      
        return sortedMap;
    }
	
	public static <K,V extends Comparable> Map<K,V> sortByValues2(Map<K,V> map){
        List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());
      
        Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {

            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
      
        //LinkedHashMap will keep the keys in the order they are inserted
        //which is currently sorted on natural ordering
        Map<K,V> sortedMap = new LinkedHashMap<K,V>();
      
        for(Map.Entry<K,V> entry: entries){
            sortedMap.put(entry.getKey(), entry.getValue());
        }
      
        return sortedMap;
    }
	
	public static <K,V extends Comparable> List<Entry<K, V>> sortByValuesToList(Map<K, V> map) {
        List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());
        
        Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {

            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
      
        //LinkedHashMap will keep the keys in the order they are inserted
        //which is currently sorted on natural ordering
        List<Entry<K, V>> sortedList = new ArrayList<Entry<K, V>>();
      
        for(Map.Entry<K,V> entry: entries){
            sortedList.add(entry);
        }
      
        return sortedList;
	}
	
	public static void sortPredictions(List<Prediction> unsorted) {
		Collections.sort(unsorted, new Comparator<Prediction>() {
			public int compare(Prediction p1, Prediction p2) {
				return Double.compare(p2.score, p1.score);
			}
		});
	}
	
	public static List<Activity> sortBySentenceId(Map<Activity, Match> map) {
		List<Activity> result = new ArrayList<Activity>();
		List<Integer> values = new ArrayList<Integer>();
		Map<Activity, Integer> map2 = new HashMap<Activity, Integer>();
		for (Activity act : map.keySet()) {
			values.add(map.get(act).getSentenceId());
			map2.put(act, map.get(act).getSentenceId());
		}
		Collections.sort(values);
		for (int sentenceId : values) {
			for (Activity key : map2.keySet()) {
				if (map2.get(key) == sentenceId) {
					map2.remove(key);
					result.add(key);
					break;
				}
			}
		}
		return result;
	}
	
	

	
	public static List<Tree> sortByFirstIndex(List<Tree> trees) {
		Map<Integer, Tree> map = new HashMap<Integer, Tree>();
		List<Integer> keys = new ArrayList<Integer>();
		for (Tree t : trees) {
			map.put(firstIndex(t), t);
			keys.add(firstIndex(t));
		}
		Collections.sort(keys);
		List<Tree> result = new ArrayList<Tree>();
		for (int i : keys) {
			result.add(map.get(i));
		}
		return result;
	}
	
	private static int firstIndex(Tree tree) {
		return ((CoreLabel) tree.getLeaves().get(0).label()).index();
	}

//Read more: http://javarevisited.blogspot.com/2012/12/how-to-sort-hashmap-java-by-key-and-value.html#ixzz33rjsnXSW
//	
//	public static <K extends Object> Set<List<K>> getPermutations(Set<K> elements) {
//		return getPermutations(new ArrayList<K>(), elements);
//	}
//	
//	
//	private static <K extends Object> Set<List<K>> getPermutations(List<K> prefix, Set<K> remainder) {
//		Set<List<K>> result = new HashSet<List<K>>();
//		if (remainder.isEmpty()) {
//			result.add(prefix);
//			return result;
//		}
//		for (K element : remainder) {
//			List<K> prefix2 = new ArrayList<K>(prefix);
//			prefix2.add(element);
//			Set<K> remainder2 = new HashSet<K>(remainder);
//			remainder2.remove(element);
//			result.addAll(getPermutations(prefix2, remainder2));
//		}
//		return result;
//	}
}
