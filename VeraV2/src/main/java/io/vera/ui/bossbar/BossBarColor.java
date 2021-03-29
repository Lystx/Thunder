package io.vera.ui.bossbar;

public enum BossBarColor {
    PINK(0),
    BLUE(1),
    RED(2),
    GREEN(3),
    YELLOW(4),
    PURPLE(5),
    WHITE(6);

    private final int id;

    public int getId() {
        return this.id;
    }

    BossBarColor(int id) {
        this.id = id;
    }

    public static BossBarColor of(int id) {
        for (BossBarColor c : values()) {
            if (c.id == id)
                return c;
        }
        throw new IllegalArgumentException("no boss bar color with id = " + id);
    }
}
