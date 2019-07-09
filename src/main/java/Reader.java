import com.opencsv.CSVReader;
import javafx.util.Pair;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class Reader {
    private List<Song> songs = new ArrayList<>();
    private List<User> users;
    private List<UserSong> userSongs = new ArrayList<>();
    private final String pathSong;
    private final String pathUser;
    private Map<String, Integer> songListened;
    private double[] columnAvgs;
    private double[] stdDeviations;
    private double[][] matrix;

    public Reader(String pathSong, String pathUser) {
        this.pathSong = pathSong;
        this.pathUser = pathUser;

        System.out.println("start reader");
        readSongs(pathSong);
        System.out.println("read songs");
        readUsers(pathUser);
        System.out.println("read users");
        songListened = userSongs.stream().collect(Collectors.groupingBy(UserSong::getSong_id, Collectors.summingInt(UserSong::getListen_count)));
        songs = songs.stream().filter(s -> songListened.get(s.getSong_id()) != null).collect(Collectors.toList());
        matrix = new double[songs.size()][5];
        computeFeatures();

    }

    private void readSongs(String pathSong) {
        try (CSVReader csvReader = new CSVReader(new FileReader(pathSong))) {
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                songs.add(new Song(values[0], values[1], values[2], values[3], Integer.parseInt(values[4])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readUsers(String pathUser) {
        try (CSVReader csvReader = new CSVReader(new FileReader(pathUser))) {
            String[] values;
            Set<User> setUsers = new HashSet<>();
            while ((values = csvReader.readNext()) != null) {
                User user = new User(values[0]);
                setUsers.add(user);
                userSongs.add(new UserSong(values[1], values[0], Integer.parseInt(values[2])));
            }
            users = new ArrayList<>(setUsers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<UserSong> getSongsByUser(String user_id) {
        return userSongs.stream().filter(x -> x.getUser_id().equals(user_id)).collect(Collectors.toList());
    }

    public List<Song> getSongs() {
        return songs;
    }

    public List<UserSong> getUserSongs() {
        return userSongs;
    }

    public Integer getMax(String user_id) {
        return userSongs.stream().filter(x -> x.getUser_id().equals(user_id)).mapToInt(UserSong::getListen_count).max().getAsInt();
    }

    public String getSongByID(String id_song) {
        return songs.stream().filter(x -> x.getSong_id().equals(id_song)).map(Song::getTitle).findFirst().get();
    }

    public Song getFullSongByID(String id_song) {
        return songs.stream().filter(x -> x.getSong_id().equals(id_song)).findFirst().get();
    }

    private double[] computeFeatureForSong(Song song, int index) {
        double[] sum = new double[5];
        for (Song sg : songs.stream().filter(s -> s.getSong_id().equals(song.getSong_id()) || s.getYear().equals(song.getYear()) || s.getArtist_name().equals(song.getArtist_name()) || s.getRelease().equals(song.getRelease())).collect(Collectors.toList())) {
            sum[0] = 1d;
            matrix[index][0] = 1d;
            if (sg.getSong_id().equals(song.getSong_id())) {
                sum[1] += songListened.get(sg.getSong_id());
                matrix[index][1] += songListened.get(sg.getSong_id());
            }
            if (sg.getRelease().equals(song.getRelease())) {
                sum[2] += songListened.get(sg.getSong_id());
                matrix[index][2] += songListened.get(sg.getSong_id());
            }
            if (sg.getArtist_name().equals(song.getArtist_name())) {
                sum[3] += songListened.get(sg.getSong_id());
                matrix[index][3] += songListened.get(sg.getSong_id());
            }
            if (sg.getYear().equals(song.getYear())) {
                sum[4] += songListened.get(sg.getSong_id());
                matrix[index][4] += songListened.get(sg.getSong_id());
            }
        }
        int cnt = getSongs().size();
        sum[1] /= cnt;
        sum[2] /= cnt;
        sum[3] /= cnt;
        sum[4] /= cnt;
        matrix[index][1] /= cnt;
        matrix[index][2] /= cnt;
        matrix[index][3] /= cnt;
        matrix[index][4] /= cnt;
        return sum;
    }

    public void computeFeatures() {
        int i = 0;
        for (Song song : songs) {
            song.setFeatures(computeFeatureForSong(song, i));
            i += 1;
        }

        getRanges(songs.size(), matrix);
        normalize(songs.size(), matrix);

        int in = 0;
        for (Song song : songs) {
            song.setFeatures(matrix[in]);
            in += 1;
        }
    }

    private void getRanges(Integer noLines, double[][] matrix) {
        columnAvgs = new double[5];
        stdDeviations = new double[5];
        for (int j = 0; j <= 4; j++) {
            Double columnAvg = 0d;
            for (int i = 0; i < noLines; i++) columnAvg += matrix[i][j];
            columnAvg = columnAvg / noLines;
            columnAvgs[j] = columnAvg;

            Double standardDeviation = 0d;
            for (int i = 0; i < noLines; i++)
                standardDeviation += (matrix[i][j] - columnAvg) * (matrix[i][j] - columnAvg);
            standardDeviation = Math.sqrt(standardDeviation / (noLines - 1));
            stdDeviations[j] = standardDeviation;
        }
    }

    private void normalize(Integer noLines, double[][] matrix) {
        for (int j = 1; j <= 4; j++) {
            for (int i = 0; i < noLines; i++) {
                matrix[i][j] = 1d * (matrix[i][j] - columnAvgs[j]) / stdDeviations[j];
            }
        }
    }


}
