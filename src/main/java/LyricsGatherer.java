
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class LyricsGatherer {

    private final static String songLyricsURL = "http://www.songlyrics.com";


    public static List<String> getSongLyrics( String band, String songTitle) throws IOException {
        List<String> lyrics= new ArrayList<String>();

        Document doc = Jsoup.connect(songLyricsURL+ "/"+band.replace(" ", "-").toLowerCase()+"/"+songTitle.replace(" ", "-").toLowerCase()+"-lyrics/").get();
        String title = doc.title();
       // System.out.println(title);
        Element p = doc.select("p.songLyricsV14").get(0);
        for (Node e: p.childNodes()) {
            if (e instanceof TextNode) {
                lyrics.add(((TextNode)e).getWholeText());
            }
        }
        return lyrics;
    }



    public static void main(String[] args) throws IOException {
       // System.out.println(LyricsGatherer.getSongLyrics("U2", "With or Without You"));
       // System.out.println(LyricsGatherer.getSongLyrics("Billy Joel", "Allentown"));
        System.out.println(LyricsGatherer.getSongLyrics("The whitest boy alive", "Fireworks"));
       // System.out.println(LyricsGatherer.getSongLyrics("Choeur Arménien de Sofia","Le Corps de Notre Seigneur"));
        System.out.println(LyricsGatherer.getSongLyrics("Moby","Slipping Away"));
        System.out.println(LyricsGatherer.getSongLyrics("Interpol","Say Hello To The Angels"));

    }
}