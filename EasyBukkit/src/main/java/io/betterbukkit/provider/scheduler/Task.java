package io.betterbukkit.provider.scheduler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @RequiredArgsConstructor @Setter
public class Task implements Runnable {

	private final boolean sync;
	private final Runnable runnable;
	private final int id;
	private final boolean repeating;

	private int runTimes;
	private boolean cancelled;
	private boolean error;

	@Override
	public void run() {
		if (cancelled || error) {
			return;
		}
		this.runTimes++;
		try {
			this.runnable.run();
		} catch (Exception e) {
			this.error = true;
			e.printStackTrace();
		}
	}


}
