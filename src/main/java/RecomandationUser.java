import java.util.*;
import java.util.stream.Collectors;

public class RecomandationUser {

    private Reader trainingReader;
    private Double[] thetaJ;
    String userRecomended;

    RecomandationUser(Reader trainingReader, String userRecomended, int noOfIterations) {
        this.trainingReader = trainingReader;
        thetaJ = new Double[5];
        Random random = new Random();
        thetaJ[0] = 1d;
        for(int i = 1; i<thetaJ.length; i+=1)
            thetaJ[i] = (double) random.nextInt(trainingReader.getMax(userRecomended));
        this.noOfIterations = noOfIterations;
        this.userRecomended = userRecomended;
    }

    private int noOfIterations;

    public Runnable runnableTask = () -> {
        for (int it = 0; it < noOfIterations; it += 1) {
                trainUserTheta(trainingReader.getSongsByUser(userRecomended), 0.000000005);
            }
    };

    public void trainUserTheta(List<UserSong> recSongs, Double learningRate) {
        for (UserSong song : recSongs) {
            double value = prediction(song.getSong_id());
            int count = song.getListen_count();
            value = Math.pow(value-count, 2);
            Song song1 = trainingReader.getFullSongByID(song.getSong_id());
            for(int i = 0; i<thetaJ.length; i+=1){
                thetaJ[i] -= learningRate*value*song1.getFeatures()[i];
            }
        }
        for(int i=0; i<thetaJ.length; i+=1)
            thetaJ[i] /= 2*trainingReader.getSongs().size();
    }

    private double prediction(String song) {
        double value = 0;
        Song song1 = trainingReader.getFullSongByID(song);
        for(int i = 1; i<song1.getFeatures().length; i+=1){
            value += song1.getFeatures()[i]*thetaJ[i];
        }
        return value;
    }

    public List<String> test(List<UserSong> recSongs, int noOfSongs) {
        TreeMap<Double, UserSong> songTreeMap = new TreeMap<>();
        for (UserSong song : recSongs) {
            Double value = prediction(song.getSong_id());
            songTreeMap.put(value, song);
        }
        List<String> songList = new ArrayList<>();
        int i = 0;
        for (Double key : songTreeMap.keySet()) {
            if (i == noOfSongs)
                break;
            songList.add(songTreeMap.get(key).getSong_id());
            i += 1;
        }
        return songList;
    }

    public List<String> test2(int noOfSongs, String userId) {
        TreeMap<Double, String> songTreeMap = new TreeMap<>();
        Map<String, Double> avgSongsCount = getAvgCount(userId);
        for (String id : avgSongsCount.keySet()) {
            Double value = prediction(id);
            songTreeMap.put(value, id);
        }
        List<String> songList = new ArrayList<>();
        int i = 0;
        for (Double key : songTreeMap.keySet()) {
            if (i == noOfSongs)
                break;
            songList.add(songTreeMap.get(key));
            i += 1;
        }
        return songList;
    }

    private Map<String, Double> getAvgCount(String userID) {
        return trainingReader.getUserSongs().stream().filter(s -> !s.getUser_id().equals(userID)).collect(Collectors.groupingBy(UserSong::getSong_id, Collectors.averagingInt(UserSong::getListen_count)));
    }

    public void printThetas() {
        System.out.println(Arrays.toString(thetaJ));
    }
}


