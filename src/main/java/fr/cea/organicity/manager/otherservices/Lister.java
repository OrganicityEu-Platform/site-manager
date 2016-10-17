package fr.cea.organicity.manager.otherservices;

import java.util.List;
import java.util.function.Function;

public abstract class Lister<T> {
	
	private List<T> elements = null;
	private long timestamp = 0;

	private final Function<T, String> getId;
	private final long ttlInSeconds;
	
	public Lister(Function<T, String> getId, long ttlInSeconds) {
		this.getId = getId;
		this.ttlInSeconds = ttlInSeconds;
	}
	
	public List<T> getElements() {
		if (needupdate()) {
			elements = retrieveElements();
			timestamp = System.currentTimeMillis();
		}
		return elements;
	}
	
	protected abstract List<T> retrieveElements();

	public T getElement(String id) {
		for (T element : getElements()) {
			if (getId.apply(element).equals(id))
				return element;
		}
		return null;
	}
	
	private boolean needupdate() {
		if (elements == null || timestamp == 0)
			return true;
		
		long cur = System.currentTimeMillis();
		if (cur - timestamp > ttlInSeconds * 1000)
			return true;
		
		return false;
	}
}
