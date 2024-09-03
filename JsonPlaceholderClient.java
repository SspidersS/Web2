import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class JsonPlaceholderClient {

    private static final String BASE_URL_USERS = "https://jsonplaceholder.typicode.com/users";
    private static final String BASE_URL_POSTS = "https://jsonplaceholder.typicode.com/posts";
    private static final String BASE_URL_COMMENTS = "https://jsonplaceholder.typicode.com/posts";

    public static void main(String[] args) {
        try {
            int userId = 1;
            int lastPostId = getLastPostId(userId);
            System.out.println("Last Post ID: " + lastPostId);

            String comments = getCommentsForPost(lastPostId);
            System.out.println("Comments for Post ID " + lastPostId + ":\n" + comments);

            writeCommentsToFile(userId, lastPostId, comments);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAllUsers() throws Exception {
        URL url = new URL(BASE_URL_USERS);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return in.lines().collect(Collectors.joining("\n"));
        }
    }

    public static String getUserById(int id) throws Exception {
        URL url = new URL(BASE_URL_USERS + "/" + id);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return in.lines().collect(Collectors.joining("\n"));
        }
    }

    public static String getUserByUsername(String username) throws Exception {
        URL url = new URL(BASE_URL_USERS + "?username=" + username);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return in.lines().collect(Collectors.joining("\n"));
        }
    }

    public static String createUser(String jsonInputString) throws Exception {
        URL url = new URL(BASE_URL_USERS);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return in.lines().collect(Collectors.joining("\n"));
        }
    }

    public static int updateUser(int id, String jsonInputString) throws Exception {
        URL url = new URL(BASE_URL_USERS + "/" + id);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        return connection.getResponseCode();
    }

    public static int deleteUser(int id) throws Exception {
        URL url = new URL(BASE_URL_USERS + "/" + id);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");

        return connection.getResponseCode();
    }

    public static int getLastPostId(int userId) throws Exception {
        URL url = new URL(BASE_URL_POSTS + "?userId=" + userId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String response = in.lines().collect(Collectors.joining("\n"));
            String[] posts = response.split("\\},\\{");
            int maxId = 0;
            for (String post : posts) {
                String[] parts = post.split("\"id\":");
                if (parts.length > 1) {
                    String idPart = parts[1].split(",")[0].trim();
                    int id = Integer.parseInt(idPart);
                    if (id > maxId) {
                        maxId = id;
                    }
                }
            }
            return maxId;
        }
    }

    public static String getCommentsForPost(int postId) throws Exception {
        URL url = new URL(BASE_URL_COMMENTS + "/" + postId + "/comments");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return in.lines().collect(Collectors.joining("\n"));
        }
    }

    public static void writeCommentsToFile(int userId, int postId, String comments) throws Exception {
        String fileName = String.format("user-%d-post-%d-comments.json", userId, postId);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(comments);
        }
        System.out.println("Comments written to file: " + fileName);
    }
}
