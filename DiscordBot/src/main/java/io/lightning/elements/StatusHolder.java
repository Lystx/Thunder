package io.lightning.elements;

import io.lightning.Lightning;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Getter
public class StatusHolder {


	private final OnlineStatus status;
	private final Activity activity;


	public static Optional<StatusHolder> overrideStatus = Optional.empty();
	public static boolean update = false;
	public static boolean showingPumpkin = false;
	public static boolean showingCustom = false;


	public static void run() {

		Random random = new Random();
		int lastranhour = -1;
		while (true) {
			try {
				if (overrideStatus.isPresent()) {
					if (update) {
						Lightning.get().getJda().getPresence().setPresence(overrideStatus.get().getStatus(), overrideStatus.get().getActivity());
						update = false;
						showingCustom = true;
					}
				} else {

					LocalDateTime now = LocalDateTime.now();
					if (now.getHour() != lastranhour || showingPumpkin || showingCustom) {
						showingPumpkin = false;
						showingCustom = false;
						lastranhour = now.getHour();

						String date = now.getDayOfMonth() + "/" + now.getMonthValue();
						switch (date) {
							case "1/1":
								Lightning.get().getJda().getPresence().setPresence(OnlineStatus.IDLE, Activity.of(Activity.ActivityType.WATCHING, "Happy new year " + now.getYear() + "!"));
								break;
							case "14/2":
								Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.WATCHING, "Being single sucks :("));
								break;
							case "8/4":
								int i = ThreadLocalRandom.current().nextInt(0, 1);
								switch (i) {
									case 0:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.WATCHING, "Happy birthday Anna"));
										break;
									case 1:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.WATCHING, "Imagine being 17!"));
										break;
									default:
										break;
								}
								break;
							case "3/10":
								Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.WATCHING, "Happy birthay to @Lystx!"));
								break;
							case "25/12":
								Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.WATCHING, "Merry Chirstmas everyone!"));
								break;
							case "31/12":
								Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.WATCHING, "countdown! Bye " + now.getYear() + "!"));
								break;
							default:
								switch (now.getHour()) {
									case 0:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.WATCHING, "through all guilds..."));
										break;
									case 1:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.DEFAULT, "Valorant but bad"));
										break;
									case 2:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.DEFAULT, "Minecraft lulW"));
										break;
									case 3:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.WATCHING, "Rebekkah Wing!"));
										break;
									case 4:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.STREAMING, "Sleeping..."));
										break;
									case 5:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.STREAMING, "Still sleeping..."));
										break;
									case 6:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.STREAMING, "Still sleeping my guy..."));
										break;
									case 7:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.DEFAULT, "Getting up..."));
										break;
									case 8:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.WATCHING, "Morning Shows"));
										break;
									case 9:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.WATCHING, "Instagram & Snapchat"));
										break;
									case 10:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.DEFAULT, "Cleaning the Server..."));
										break;
									case 11:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.DEFAULT, "Preparing some stuff..."));
										break;
									case 12:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.LISTENING, "music"));
										break;
									case 13:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.WATCHING, "Eating lunch"));
										break;
									case 14:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.DEFAULT, "It's 2pm!"));
										break;
									case 15:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.DEFAULT, "Cleaning up the database!"));
										break;
									case 16:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.DEFAULT, "Day's almost over!"));
										break;
									case 17:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.DEFAULT, "Tea Time my guys!"));
										break;
									case 18:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.STREAMING, "on Twitch!"));
										break;
									case 19:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.DEFAULT, "First are going to sleep!"));
										break;
									case 20:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.DEFAULT, "It's 8pm my guys"));
										break;
									case 21:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.DEFAULT, "It's 8pm my guys"));
										break;
									case 22:
										Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.DEFAULT, "Some work left to do?"));
										break;
									case 23:
										switch (random.nextInt(6)) {
											case 0:
												Lightning.get().getJda().getPresence().setPresence(OnlineStatus.IDLE, Activity.of(Activity.ActivityType.LISTENING, "to Lystx raging..."));
												break;
											case 1:
												Lightning.get().getJda().getPresence().setPresence(OnlineStatus.IDLE, Activity.of(Activity.ActivityType.DEFAULT, "Error404 no status found!"));
												break;
											case 2:
												Lightning.get().getJda().getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.of(Activity.ActivityType.WATCHING, "Porn"));
												break;
											case 3:
												Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.DEFAULT, "with my Creator!"));
												break;
											case 4:
												Lightning.get().getJda().getPresence().setPresence(OnlineStatus.IDLE, Activity.of(Activity.ActivityType.LISTENING, "to rain sounds"));
												break;
											case 5:
												Lightning.get().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.WATCHING, "some movies on Netflix"));
												break;
											default:
												break;
										}
										break;
									default:
										break;
								}
								break;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				TimeUnit.SECONDS.sleep(120);
			} catch (InterruptedException ignore) {

			}
		}

	}
}
