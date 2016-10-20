package fr.cea.organicity.manager.otherservices;

import lombok.Data;

@Data
public class ThirdPartyResult<T> {
	// last success stats
	private final T       lastSuccessResult;
	private final long    lastSuccessTimestamp;
	
	// last call stats
	private final boolean lastCallSucess;
	private final long    lastCallduration;
	private final long    lastCallTimestamp;
	
	public boolean hasAlreadySucceed() {
		return lastSuccessTimestamp != 0;
	}
}
