import com.sun.javafx.collections.MappingChange;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PopularityBasedRecommendation {
    private List<UserSong> userSongs;
    private List<Song> songs;

    public PopularityBasedRecommendation(List<UserSong> userSongs, List<Song> songs) {
        this.userSongs = userSongs;
        this.songs = songs;
    }

    public List<Song> getRecomandation(Integer year, Integer topn) {
        List<Song> filterSongs = songs.stream()
                .filter(s -> s.getYear() >= year || s.getYear() == 0)
                .collect(Collectors.toList());
        Map<String, Integer> us = userSongs.stream()
                .collect(Collectors.groupingBy(UserSong::getSong_id, Collectors.summingInt(UserSong::getListen_count)));

        List<Map.Entry<String, Integer>> list = new ArrayList<>(us.entrySet());

        list.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));
        List<Song> rez = new ArrayList<>();
        Song song;
        int k=0;
        for(Map.Entry<String,Integer> x:list){
            song = findSongById(filterSongs, x.getKey());
            if(song!=null) {
                k++;
                rez.add(song);
                System.out.print(song + " ");
                System.out.println(x);
                if(k==topn)break;
            }
        }
        return rez;
    }

    private Song findSongById(List<Song> songs, String id) {
        return songs.stream().filter(s -> s.getSong_id().equals(id)).findAny().orElse(null);
    }

    public void findMostListenedSong(String userId){
        List<UserSong> songList = userSongs.stream().filter(s->s.getUser_id().equals(userId)).collect(Collectors.toList());
        //System.out.println("Done collecting");
        Collections.sort(songList, new SortByListenCount());
       // System.out.println("Done sorting");
        Song s1 = findSongById(songs, songList.get(3).getSong_id());
        Song s2 = findSongById(songs, songList.get(5).getSong_id());
        Song s3 = findSongById(songs, songList.get(6).getSong_id());
        CountTitleApparitions(s1,s1,wordsCount1);
        CountTitleApparitions(s1,s2,wordsCount2);
        CountTitleApparitions(s1,s3,wordsCount3);

        CountTitleApparitions(s2,s1,wordsCount1);
        CountTitleApparitions(s2,s2,wordsCount2);
        CountTitleApparitions(s2,s3,wordsCount3);

        CountTitleApparitions(s3,s1,wordsCount1);
        CountTitleApparitions(s3,s2,wordsCount2);
        CountTitleApparitions(s3,s3,wordsCount3);

        Map<String, Double> termFrequency1 = GetTermFrequency(wordsCount1);
        Map<String, Double> termFrequency2 = GetTermFrequency(wordsCount2);
        Map<String, Double> termFrequency3 = GetTermFrequency(wordsCount3);

        Map<String, Double> allTermFrequencies = new HashMap<>();

        for (String s: termFrequency1.keySet()) {
            allTermFrequencies.put(s,termFrequency1.get(s));
        }

        for (String s: termFrequency2.keySet()) {
            allTermFrequencies.computeIfPresent(s,(k,v)->v+termFrequency2.get(s));
        }

        for (String s: termFrequency3.keySet()) {
            allTermFrequencies.computeIfPresent(s,(k,v)->v+termFrequency3.get(s));
        }

        ValueComparator bvc = new ValueComparator(allTermFrequencies);
        TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(bvc);
        sorted_map.putAll(allTermFrequencies);
        List<String> mostFrequentWords = new ArrayList<>();
        mostFrequentWords.add((String) allTermFrequencies.keySet().toArray()[0]);
        mostFrequentWords.add((String) allTermFrequencies.keySet().toArray()[1]);
        mostFrequentWords.add((String) allTermFrequencies.keySet().toArray()[2]);

        // System.out.println("results: " + sorted_map);
        List<Song> list = GetSongsByTermFrequency(mostFrequentWords,s1,s2,s3);
        DisplayList(list);

    }


    private void DisplayList(List<Song> songs1){
        System.out.println("Songs list: ");
        for (Song s: songs1) {
            System.out.println(s);
        }
    }

    private List<Song> GetSongsByTermFrequency(List<String> words, Song s1,Song s2,Song s3){
        List<Song> songsList = new ArrayList<>();
        for (Song s: songs) {
            if( !s.getSong_id().equals(s1.getSong_id()) && !s.getSong_id().equals(s2.getSong_id()) && !s.getSong_id().equals(s3.getSong_id()) && songsList.size() < 11){
                if(s.getTitle().toLowerCase().contains(words.get(0).toLowerCase()) || s.getTitle().toLowerCase().contains(words.get(1).toLowerCase()) || s.getTitle().toLowerCase().contains(words.get(2).toLowerCase()))
                    songsList.add(s);
            }
        }
        return songsList;
    }


    private Map<String, Double> GetTermFrequency(Map<String, Integer> map){
        Map<String, Double> termFrequency1 = new HashMap<>();
        int count = GetSumValue(map);
        for (String s:map.keySet()) {
            termFrequency1.put(s,map.get(s)*1.0/count);
        }
        return termFrequency1;
    }


    private int GetSumValue(Map<String, Integer> map){
        int sum = 0;
        for (String s: map.keySet()) {
            sum += map.get(s);
        }
        return sum;
    }

    private Map<String, Integer> wordsCount1 = new HashMap<>();
    private Map<String, Integer> wordsCount2 = new HashMap<>();
    private Map<String, Integer> wordsCount3 = new HashMap<>();


    private void CountTitleApparitions(Song s, Song song, Map<String, Integer> words){
        try {
            List<String> lyrics1 = LyricsGatherer.getSongLyrics(song.getArtist_name(),song.getTitle());
            for (String string: lyrics1) {
                String[] strs = string.split(" ");
                for (String word: strs) {
                    if(!word.equals(" ") && !word.equals("  ") && !(word.toLowerCase().equals("the")) && !(word.toLowerCase().equals("but")) && !(word.toLowerCase().equals("this")) && !(word.toLowerCase().equals("because"))
                                && !(word.toLowerCase().equals("in"))) {
                        words.putIfAbsent(word, 1);
                        words.computeIfPresent(word, (k, v) -> v + 1);
                    }
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}


class SortByListenCount implements Comparator<UserSong>{

    @Override
    public int compare(UserSong o1, UserSong o2) {
        return o2.getListen_count() - o1.getListen_count();
    }

}

class ValueComparator implements Comparator<String> {
    Map<String, Double> base;

    public ValueComparator(Map<String, Double> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with
    // equals.
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}