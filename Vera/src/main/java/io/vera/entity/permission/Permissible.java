
package io.vera.entity.permission;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface Permissible {

    boolean hasPermission(String perm);

    void addPermission(String perm);

    boolean removePermission(String perm);

    void setOp(boolean op);

    boolean isOp();
}
