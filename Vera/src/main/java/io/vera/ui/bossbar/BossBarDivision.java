
package io.vera.ui.bossbar;

import lombok.Getter;


@Getter
public enum BossBarDivision {

    NO_DIVISION(0, 0),
    NOTCHES_6(1, 6),
    NOTCHES_10(2, 10),
    NOTCHES_12(3, 12),
    NOTCHES_20(4, 20);


    private final int id;

    private final int notches;

    BossBarDivision(int id, int notches) {
        this.id = id;
        this.notches = notches;
    }

    public static BossBarDivision of(int id) {
        for (BossBarDivision c : values())
            if (c.id == id)
                return c;
        throw new IllegalArgumentException("no boss bar division with id = " + id);
    }

}
