import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        Reader trainingReader = new Reader("song_data.csv", "user_data.csv");

        RecomandationUser recomandationUser = new RecomandationUser(trainingReader,"cfb1faf96b2902045c43293878e7ee384584083a", 1000);
        PopularityBasedRecommendation popularityBasedRecommendation = new PopularityBasedRecommendation(trainingReader.getUserSongs(), trainingReader.getSongs());

        popularityBasedRecommendation.getRecomandation(2000, 10);

        ExecutorService executors = Executors.newFixedThreadPool(25);
        try {
            executors.submit(recomandationUser.runnableTask).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        executors.shutdown();

  /*      List<String> stringList = recomandationUser.test(trainingReader.getSongsByUser("5a905f000fc1ff3df7ca807d57edb608863db05d"),10);

        recomandationUser.printThetas();

        System.out.println("Recommended by another user: ");
        for (String str: stringList) {
            System.out.println(trainingReader.getSongByID(str));
        }

        List<String> stringList2 = recomandationUser.test2(10, "cfb1faf96b2902045c43293878e7ee384584083a");
        System.out.println("Recommended based on his preferences");
        for (String str: stringList2) {
            System.out.println(trainingReader.getSongByID(str));
        }
*/
        System.out.println("Based on Term-Frequency of a song title");
        popularityBasedRecommendation.findMostListenedSong("cfb1faf96b2902045c43293878e7ee384584083a");

    }
}
