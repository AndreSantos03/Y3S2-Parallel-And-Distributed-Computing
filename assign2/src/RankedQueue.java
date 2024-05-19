import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.*;

public class RankedQueue {
    private List<UserEntry> queue = new ArrayList<>();
    private Lock lock = new ReentrantLock();
    private Condition playerAvailable = lock.newCondition();
    private int maxLevelDifference;
    private long waitTime;
    private int playersPerGame;

    public RankedQueue(int playersPerGame, int initialMaxLevelDifference, long initialWaitTime) {
        this.playersPerGame = playersPerGame;
        this.maxLevelDifference = initialMaxLevelDifference;
        this.waitTime = initialWaitTime;
    }

    public void addPlayer(String username, SocketChannel socket, int level) {
        lock.lock();
        try {
            queue.add(new UserEntry(username, socket, level));
            playerAvailable.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public List<UserEntry> findMatch() throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() < playersPerGame) {
                playerAvailable.await();
            }
        } finally {
            lock.unlock();
        }

        List<UserEntry> team = null;
        long startTime = System.currentTimeMillis();
        while (team == null) {
            team = attemptMatch();
            if (team == null) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime > waitTime) {
                    maxLevelDifference++;
                    startTime = System.currentTimeMillis(); // Reset wait time
                }
                Thread.sleep(1000); // Wait a bit before retrying
            }
        }
        return team;
    }

    private List<UserEntry> attemptMatch() {
        lock.lock();
        try {
            Collections.sort(queue, Comparator.comparingInt(UserEntry::getLevel));
            for (int i = 0; i <= queue.size() - playersPerGame; i++) {
                List<UserEntry> potentialMatch = queue.subList(i, i + playersPerGame);
                int minLevel = potentialMatch.get(0).getLevel();
                int maxLevel = potentialMatch.get(potentialMatch.size() - 1).getLevel();
                if (maxLevel - minLevel <= maxLevelDifference) {
                    List<UserEntry> match = new ArrayList<>(potentialMatch);
                    queue.removeAll(potentialMatch);
                    return match;
                }
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    public void startMatchmaking(ExecutorService executorService, Server server) {
        executorService.submit(() -> {
            while (true) {
                try {
                    List<UserEntry> team = findMatch();
                    if (team != null) {
                        server.rankedMode(team,executorService);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        });
    }

    public static class UserEntry {
        private String username;
        private SocketChannel socket;
        private int level;

        public UserEntry(String username, SocketChannel socket, int level) {
            this.username = username;
            this.socket = socket;
            this.level = level;
        }

        public String getUsername() {
            return username;
        }

        public SocketChannel getSocket() {
            return socket;
        }

        public int getLevel() {
            return level;
        }
    }
}