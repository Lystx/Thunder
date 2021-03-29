package io.betterbukkit.provider.util;

import io.betterbukkit.EasyBukkit;
import io.betterbukkit.provider.item.Substance;
import io.betterbukkit.provider.world.Block;
import io.betterbukkit.provider.world.Position;
import io.betterbukkit.provider.world.World;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class BlockUtils extends Util<Block, org.bukkit.block.Block> {

    @Override
    public Block from(org.bukkit.block.Block block) {
        return new Block() {
            @Override
            public byte getData() {
                return block.getData();
            }

            @Override
            public Substance getSubstance() {
                return Substance.valueOf(block.getType().name());
            }

            @Override
            public Position getPosition() {
                return Util.get(PositionUtils.class).from(block.getLocation());
            }

            @Override
            public World getWorld() {
                return Util.get(WorldUtils.class).from(block.getWorld());
            }

            @Override
            public void setSubstance(Substance substance) {
                block.setType(Material.valueOf(substance.name()));
            }

            @Override
            public void setData(byte data) {
                block.setData(data);
            }

            //TODO: FIX BLOCK BREAKING 16.03.2021
            @Override
            public void breakBlock(long delayPerBreak) {
                int[] i = new int[1];

                i[0] = 1;
                EasyBukkit.getInstance().getScheduler().scheduleRepeatingTask(() -> {
                }, delayPerBreak, delayPerBreak, false, task -> {
                    PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(new Random().nextInt(2000), new BlockPosition(getPosition().getX(), getPosition().getY(), getPosition().getZ()), i[0]);

                    EasyBukkit.getInstance().getPlayers().forEach(player -> player.sendPacket(packet));
                    i[0] = i[0] + 1;
                    if (i[0] >= 10) {
                        task.setCancelled(true);
                        EasyBukkit.getInstance().getScheduler().runTask(() -> {
                            System.out.println(true);
                            block.breakNaturally();
                            block.setType(Material.AIR);
                        });
                    }
                    return task;
                });
            }

            @Override
            public Block getOtherBlock(int addX, int addY, int addZ) {
                return Util.get(BlockUtils.class).from(block.getRelative(addX, addY, addZ));
            }

            @Override
            public List<Block> getBlocksAround(int radius) {
                List<Block> list = new LinkedList<>();

                Position term = getPosition();
                for (int x = term.toBlock(term.getX()) - radius; x < term.getX() + radius; x++) {
                    for (int y = term.toBlock(term.getY()) - radius; y < term.getY() + radius; y++) {
                        for (int z = term.toBlock(term.getZ()) - radius; z < term.getZ() + radius; z++) {
                            list.add(term.getWorld().getBlockAt(x , y , z));
                        }
                    }
                }
                return list;
            }
        };
    }

    @Override
    public org.bukkit.block.Block to(Block block) {
        return new org.bukkit.block.Block() {
            @Override
            public byte getData() {
                return block.getData();
            }

            @Override
            public org.bukkit.block.Block getRelative(int i, int i1, int i2) {
                return Util.get(BlockUtils.class).to(block.getWorld().getBlockAt(i, i1, i2));
            }

            @Override
            public org.bukkit.block.Block getRelative(BlockFace blockFace) {
                return null;
            }

            @Override
            public org.bukkit.block.Block getRelative(BlockFace blockFace, int i) {
                return null;
            }

            @Override
            public Material getType() {
                return Material.valueOf(block.getSubstance().name());
            }

            @Override
            public int getTypeId() {
                return Material.valueOf(block.getSubstance().name()).getId();
            }

            @Override
            public byte getLightLevel() {
                return 0;
            }

            @Override
            public byte getLightFromSky() {
                return 0;
            }

            @Override
            public byte getLightFromBlocks() {
                return 0;
            }

            @Override
            public org.bukkit.World getWorld() {
                return Util.get(WorldUtils.class).to(block.getWorld());
            }

            @Override
            public int getX() {
                return (int) block.getPosition().getX();
            }

            @Override
            public int getY() {
                return (int) block.getPosition().getY();
            }

            @Override
            public int getZ() {
                return (int) block.getPosition().getZ();
            }

            @Override
            public Location getLocation() {
                return Util.get(PositionUtils.class).to(block.getPosition());
            }

            @Override
            public Location getLocation(Location location) {
                return null;
            }

            @Override
            public Chunk getChunk() {
                return null;
            }

            @Override
            public void setData(byte b) {
                block.setData(b);
            }

            @Override
            public void setData(byte b, boolean b1) {
                setData(b);
            }

            @Override
            public void setType(Material material) {
                block.setSubstance(Substance.valueOf(material.name()));
            }

            @Override
            public void setType(Material material, boolean b) {
                setType(material);
            }

            @Override
            public boolean setTypeId(int i) {
                return false;
            }

            @Override
            public boolean setTypeId(int i, boolean b) {
                return false;
            }

            @Override
            public boolean setTypeIdAndData(int i, byte b, boolean b1) {
                return false;
            }

            @Override
            public BlockFace getFace(org.bukkit.block.Block block) {
                return null;
            }

            @Override
            public BlockState getState() {
                return null;
            }

            @Override
            public Biome getBiome() {
                return null;
            }

            @Override
            public void setBiome(Biome biome) {

            }

            @Override
            public boolean isBlockPowered() {
                return false;
            }

            @Override
            public boolean isBlockIndirectlyPowered() {
                return false;
            }

            @Override
            public boolean isBlockFacePowered(BlockFace blockFace) {
                return false;
            }

            @Override
            public boolean isBlockFaceIndirectlyPowered(BlockFace blockFace) {
                return false;
            }

            @Override
            public int getBlockPower(BlockFace blockFace) {
                return 0;
            }

            @Override
            public int getBlockPower() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean isLiquid() {
                return false;
            }

            @Override
            public double getTemperature() {
                return 0;
            }

            @Override
            public double getHumidity() {
                return 0;
            }

            @Override
            public PistonMoveReaction getPistonMoveReaction() {
                return null;
            }

            @Override
            public boolean breakNaturally() {
                block.breakBlock(10L);
                return false;
            }

            @Override
            public boolean breakNaturally(ItemStack itemStack) {
                return false;
            }

            @Override
            public Collection<ItemStack> getDrops() {
                return null;
            }

            @Override
            public Collection<ItemStack> getDrops(ItemStack itemStack) {
                return null;
            }

            @Override
            public void setMetadata(String s, MetadataValue metadataValue) {

            }

            @Override
            public List<MetadataValue> getMetadata(String s) {
                return null;
            }

            @Override
            public boolean hasMetadata(String s) {
                return false;
            }

            @Override
            public void removeMetadata(String s, Plugin plugin) {

            }
        };
    }
}
