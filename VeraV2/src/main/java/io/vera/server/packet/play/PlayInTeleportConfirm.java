package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketIn;
import io.vera.server.util.Cache;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class PlayInTeleportConfirm extends PacketIn {
  private static final Cache<NetClient, IdBlock> TELEPORT_ID;
  
  static {
    TELEPORT_ID = new Cache(30000L, (client, id) -> {
          int block = id.getBlockSize();
          int cur = id.getCount();
          if (cur < block)
            client.disconnect("No teleport response"); 
          return Boolean.valueOf((cur == block));
        });
  }
  
  private static class IdBlock {
    private volatile int counter;
    
    private volatile int checkedIn;
    
    private IdBlock() {}
    
    private static final AtomicIntegerFieldUpdater<IdBlock> COUNTER = AtomicIntegerFieldUpdater.newUpdater(IdBlock.class, "counter");
    
    private static final AtomicIntegerFieldUpdater<IdBlock> CHECK_IN = AtomicIntegerFieldUpdater.newUpdater(IdBlock.class, "checkedIn");
    
    int checkOut() {
      return COUNTER.getAndAdd(this, 1);
    }
    
    boolean checkIn(int id) {
      boolean good = (COUNTER.get((T)this) >= id);
      if (good)
        CHECK_IN.addAndGet(this, 1); 
      return good;
    }
    
    int getBlockSize() {
      return COUNTER.get(this);
    }
    
    int getCount() {
      return CHECK_IN.get(this);
    }
  }
  
  public static int query(NetClient client) {
    IdBlock block = (IdBlock)TELEPORT_ID.get(client, () -> new IdBlock());
    return block.checkOut();
  }
  
  public PlayInTeleportConfirm() {
    super(PlayInTeleportConfirm.class);
  }
  
  public void read(ByteBuf buf, NetClient client) {
    int id = NetData.rvint(buf);
    IdBlock block = (IdBlock)TELEPORT_ID.get(client);
    if (block != null)
      if (block.checkIn(id)) {
        client.getPlayer().resumeLogin();
      } else {
        client.disconnect("Mismatched confirmation ID (" + id + ')');
      }  
  }
}
