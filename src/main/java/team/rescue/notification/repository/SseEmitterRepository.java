package team.rescue.notification.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepository {

	private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
	private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

	public SseEmitter save(String id, SseEmitter sseEmitter) {
		emitters.put(id, sseEmitter);
		return sseEmitter;
	}

	public void saveEventCache(String id, Object event) {
		eventCache.put(id, event);
	}

	public Optional<SseEmitter> findById(String id) {
		return Optional.ofNullable(emitters.get(id));
	}

	public void deleteById(String id) {
		emitters.remove(id);
	}

	public Map<String, SseEmitter> findAllEmitterStartsWithId(String email) {
		return emitters.entrySet().stream()
				.filter(entry -> entry.getKey().startsWith(email))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public Map<String, Object> findAllEventCacheStartsWithId(String email) {
		return eventCache.entrySet().stream()
				.filter(entry -> entry.getKey().startsWith(email))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
