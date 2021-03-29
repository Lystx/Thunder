package io.vera.entity.permission;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface Permissible {

    boolean hasPermission(String paramString);

    void addPermission(String paramString);

    boolean removePermission(String paramString);

    void setOp(boolean paramBoolean);

    boolean isOp();
}
