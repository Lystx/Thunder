package io.betterbukkit.provider.scheduler;


import io.betterbukkit.EasyBukkit;
import io.betterbukkit.elements.Consumer;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Scheduler  {

	private static Scheduler instance;
	private final Map<Integer, Task> schedulerMap;

	public Scheduler(EasyBukkit bukkit) {
		this.schedulerMap = new ConcurrentHashMap<>();
	}

	public Task getTask(int id) {
		return schedulerMap.getOrDefault(id, null);
	}

	public void cancelTask(int id) {
		this.cancelTask(this.getTask(id));
	}

	public void cancelTask(Task id) {
		if (id != null) {
			id.setCancelled(true);
			schedulerMap.remove(id.getId());
		}
	}

	public void cancelTasks() {
		for (Integer r : schedulerMap.keySet()) {
			schedulerMap.get(r).setCancelled(true);
		}
		schedulerMap.clear();
	}

	public int scheduleAsyncWhile(Runnable run, long delay, long repeat) {
		int taskid = new Random().nextInt(2147483647);
		Task task = new Task(true, run, taskid, false);
		this.schedulerMap.put(taskid, task);
		return task.getId();
	}

	public Task scheduleRepeatingTaskForTimes(Runnable task, long delay, long period, long times) {
		return scheduleRepeatingTaskForTimes(task, delay, period, times, false);
	}

	public Task scheduleRepeatingTaskAsync(Runnable task, long delay, long period, long times) {
		return scheduleRepeatingTaskForTimes(task, delay, period, times, true);
	}

	public Task scheduleRepeatingTask(Runnable task, long delay, long period) {
		return scheduleRepeatingTask(task, delay, period, false);
	}

	public Task scheduleRepeatingTaskAsync(Runnable task, long delay, long period) {
		return scheduleRepeatingTask(task, delay, period, true);
	}

	public Task runTask(Runnable task) {
		Task task1 = this.runTask(task, false, false);
		new Thread(() -> { cancelTask(task1);Thread.interrupted();}).start();
		return task1;
	}

	public Task runTaskAsync(Runnable task) {
		Task task1 = runTask(task, true, false);
		new Thread(() -> { task1.run();cancelTask(task1);Thread.interrupted(); }).start();
		return task1;
	}

	public Task scheduleDelayedTask(Runnable task, long delay) {
		return delayTask(task, delay, false);
	}

	public Task scheduleDelayedTaskAsync(Runnable task, long delay) {
		return delayTask(task, delay, true);
	}

	private Task runTask(Runnable task, boolean async, boolean multipleTimes) {
		if (task == null) {
			return null;
		}
		int id = 0;
		while (true) {
			if (!schedulerMap.containsKey(id)) {
				id++;
				break;
			}
		}
		try {
			schedulerMap.put(id, new Task(!async, task, id, multipleTimes));
		} catch (Exception e) {
			//ignoring this error
		}
		return schedulerMap.get(id);
	}

	public Task delayTask(Runnable task, long delay, boolean async) {
		if (delay < 0) {
			return null;
		}
		Task t = runTask(task, async, false);
		new Timer().scheduleAtFixedRate(new TimerTask() {
			public void run() {
					t.run();
					cancelTask(t);
					cancel();
					Thread.interrupted();
			}
		}, delay * 50,1);
		return t;
	}

	public Task scheduleRepeatingTask(Runnable task, long delay, long period, boolean async) {
		return this.scheduleRepeatingTask(task, delay, period, async, null);
	}

	public Task scheduleRepeatingTask(Runnable task, long delay, long period, boolean async, Consumer<Task> taskConsumer) {
		if (period < 0) {
			return null;
		}
		if (delay < 0) {
			return null;
		}
		Task t = runTask(task, async, true);
		new Timer().scheduleAtFixedRate(new TimerTask() {
			public void run() {
				t.run();
				if (taskConsumer != null) {
					final Task consume = taskConsumer.consume(t);
					if (consume.isCancelled()) {
						cancelTask(t);
						cancel();
						Thread.interrupted();
					}
				} else {
					if (t.isCancelled()) {
						cancelTask(t);
						cancel();
						Thread.interrupted();
					}
				}
			}
		}, delay * 50, period * 50);
		return t;
	}


	private Task scheduleRepeatingTaskForTimes(Runnable task, long delay, long period, final long times, boolean async) {
		if (times <= 0) {
			return null;
		}
		if (period < 0) {
			return null;
		}
		if (delay < 0) {
			return null;
		}
		final Task t = runTask(task, async, true);
		new Timer().scheduleAtFixedRate(new TimerTask() {
			public void run() {
					t.run();
					if(t.isCancelled()||t.getRunTimes()>=times) {
					cancelTask(t);
					cancel();
					Thread.interrupted();
					}
			}
		}, delay * 50, period * 50);
		return t;
	}

	public static Scheduler getInstance() {
		if (instance == null) {
			instance = new Scheduler(EasyBukkit.getInstance());
		}
		return instance;
	}

	public static void schedule(Object object, Runnable runnable) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String method = ste[ste.length - 1 - 1].getMethodName();
		try {
			Method declaredMethod = object.getClass().getDeclaredMethod(method);
			Schedule schedule = declaredMethod.getAnnotation(Schedule.class);
			if (schedule.period() != -1L) {
				getInstance().scheduleRepeatingTask(runnable, schedule.delay(), schedule.period(), !schedule.sync());
			} else {
				getInstance().delayTask(runnable, schedule.delay(), !schedule.sync());
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}