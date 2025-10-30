package app.simplexdev.stalker.audio;

import app.simplexdev.stalker.Singletons;
import app.simplexdev.stalker.Stalker;
import dev.omialien.voicechatrecording.api.IRecordedAudio;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AudioStorage {
    public static final int SAMPLE_RATE = 48000;
    private final Random rnd;
    private final Map<UUID, List<IRecordedAudio>> storedAudios;

    public AudioStorage() {
        rnd = new Random();
        storedAudios = new ConcurrentHashMap<>();
    }

    public int getTotalAudioCount() {
        return storedAudios.values().stream().mapToInt(List::size).sum();
    }

    public void addAudio(IRecordedAudio audio) {
        if (!storedAudios.containsKey(audio.getPlayerUUID())) {
            storedAudios.put(audio.getPlayerUUID(), new LinkedList<>());
        }

        audio.saveAudio(Stalker.MODID);
        storedAudios.get(audio.getPlayerUUID()).add(audio);
    }

    public IRecordedAudio getRandomAudio(UUID player, boolean remove) {
        List<IRecordedAudio> recs = storedAudios.get(player);
        if (recs == null || recs.isEmpty()) {
            return null;
        }
        int idx = rnd.nextInt(recs.size());
        IRecordedAudio audio = recs.get(idx);
        if (remove && getTotalAudioCount() > 16) {
            storedAudios.get(player).remove(audio);
            Singletons.getRecordingApi().ifPresent(s -> s.unsaveAudio(Stalker.MODID, audio));
        }
        return audio;
    }

    public IRecordedAudio getRandomAudio(Predicate<UUID> includePlayer, boolean remove) {
        List<IRecordedAudio> total = new LinkedList<>();
        storedAudios.keySet().stream().filter(includePlayer).forEach((uuid) -> {
            total.addAll(storedAudios.get(uuid));
        });
        if (total.isEmpty()) {
            return null;
        }
        int randomIndex = rnd.nextInt(total.size());
        IRecordedAudio randomAudio = total.get(randomIndex);
        if (remove && getTotalAudioCount() > 16) {
            storedAudios.get(randomAudio.getPlayerUUID()).remove(randomAudio);
            Singletons.getRecordingApi().ifPresent(s -> s.unsaveAudio(Stalker.MODID, randomAudio));
        }
        return randomAudio;
    }

    public IRecordedAudio getRandomAudio(boolean remove) {
        List<IRecordedAudio> total = storedAudios.values().stream().flatMap(Collection::stream).toList();
        if (total.isEmpty()) {
            return null;
        }
        int randomIndex = rnd.nextInt(total.size());
        IRecordedAudio randomAudio = total.get(randomIndex);
        if (remove && getTotalAudioCount() > 16) {
            storedAudios.get(randomAudio.getPlayerUUID()).remove(randomAudio);
            Singletons.getRecordingApi().ifPresent(s -> s.unsaveAudio(Stalker.MODID, randomAudio));
        }
        return randomAudio;
    }

    public void removeRandomAudio() {
        List<IRecordedAudio> total = storedAudios.values().stream().flatMap(Collection::stream).toList();
        if (total.isEmpty()) {
            return;
        }
        IRecordedAudio toRemove = total.get(rnd.nextInt(total.size()));
        storedAudios.get(toRemove.getPlayerUUID()).remove(toRemove);
        Singletons.getRecordingApi().ifPresent(s -> s.unsaveAudio(Stalker.MODID, toRemove));
    }

    public void savePlayerAudios(UUID uuid) {
        List<IRecordedAudio> recs = storedAudios.get(uuid);
        if (recs != null && !recs.isEmpty()) {
            recs.forEach(audio -> {
                audio.saveAudio(Stalker.MODID);
            });
        }
    }

    public void saveAudios() {
        Set<IRecordedAudio> audios = storedAudios.values().stream().flatMap(List::stream).collect(Collectors.toSet());
        for (IRecordedAudio audio : audios) {
            audio.saveAudio(Stalker.MODID);
        }
    }

    /**
     * Load all audios for a specific player
     *
     * @param uuid Player UUID
     */
    public void loadPlayerAudios(UUID uuid) {
        // TODO how to deal with lots of audios being loaded when the limit has already been reached?
        // My solution: load one by one, and if the limit is reached, remove 8 random audios to make space

        Singletons.getRecordingApi().ifPresent(api ->
                api.loadPlayerAudios(uuid, (audio) ->
                        Singletons.getAudioStorage().ifPresent(a ->
                        {
                            if (a.getTotalAudioCount() < 256) {
                                a.addAudio(audio);
                            } else {
                                for (int i = 0; i < 8; i++) {
                                    a.removeRandomAudio();
                                }
                                Stalker.logger().info("Storage full, removed 8 random audios to make space for player {}", uuid);
                                a.addAudio(audio);
                            }
                        })));
    }
}
