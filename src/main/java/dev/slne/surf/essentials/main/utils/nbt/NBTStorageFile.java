package dev.slne.surf.essentials.main.utils.nbt;

import com.google.common.collect.Lists;
import dev.slne.surf.essentials.SurfEssentials;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringTag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class NBTStorageFile {

    // the file to use
    private final File file;
    // the TagCompound containing the files content
    private CompoundTag tagCompound = new CompoundTag();

    public NBTStorageFile(File file) {
        this.file = file;
    }

    // two optional constructors:
    public NBTStorageFile(String folder, String name) {
        this(new File(folder, name + ".dat"));
    }

    public NBTStorageFile(String path) {
        this(new File(path));
    }

    public void read() {
        try {
            // if the file exists we read it
            if(file.exists()) {
                FileInputStream fileinputstream = new FileInputStream(file);
                tagCompound = NbtIo.read(file);
                fileinputstream.close();

            } else {
                // else we create an empty TagCompound
                clear();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    public void clear() {
        tagCompound = new CompoundTag();
    }

    public void write() {
        try {

            if(!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fileoutputstream = new FileOutputStream(file);
            NbtIo.write(tagCompound, file);
            fileoutputstream.close();

        } catch(IOException e) {
            SurfEssentials.logger().error(file.getPath());
            e.printStackTrace();
        }

    }

    public void setStringList(String key, Collection<String> value) {
        // create a new List. This list can contain any NBTBase

        ListTag list = new ListTag();

        for(String s : value) {
            // if you want to store something else than a String, you create an other NBTbase here.
            // currently the NBTBases are: primitives, String, List and the compound itself.
            list.add(StringTag.valueOf(s));
        }
        tagCompound.put(key, list);
    }

    public List<String> getStringList(String key) {
        List<String> result = Lists.newArrayList();
        net.minecraft.nbt.
        Tag base = tagCompound.get(key);
        if(base != null && base.getClass() == ListTag.class) {

            ListTag list = (ListTag) base;
            for(int i = 0; i < list.size(); i++) {
                result.add(list.getString(i));
            }
        }
        return result;
    }

    public Set<String> getKeys() {
        return tagCompound.getAllKeys();
    }
    public boolean hasKey(String key) {
        return tagCompound.contains(key);
    }
    public boolean isEmpty() {
        return tagCompound.isEmpty();
    }
    public void remove(String key) {
        tagCompound.remove(key);
    }

    public boolean getBoolean(String key) {
        return tagCompound.getBoolean(key);
    }
    public double getDouble(String key) {
        return tagCompound.getDouble(key);
    }
    public float getFloat(String key) {
        return tagCompound.getFloat(key);
    }
    public int getInt(String key) {
        return tagCompound.getInt(key);
    }
    public int[] getIntArray(String key) {
        return tagCompound.getIntArray(key);
    }
    public long getLong(String key) {
        return tagCompound.getLong(key);
    }
    public short getShort(String key) {
        return tagCompound.getShort(key);
    }
    public String getString(String key) {
        return tagCompound.getString(key);
    }

    public void setBoolean(String key, boolean value) {
        tagCompound.putBoolean(key, value);
    }
    public void setDouble(String key, double value) {
        tagCompound.putDouble(key, value);
    }
    public void setFloat(String key, float value) {
        tagCompound.putFloat(key, value);
    }
    public void setInt(String key, int value) {
        tagCompound.putInt(key, value);
    }
    public void setIntArray(String key, int[] value) {
        tagCompound.putIntArray(key, value);
    }
    public void setLong(String key, long value) {
        tagCompound.putLong(key, value);
    }
    public void setShort(String key, short value) {
        tagCompound.putShort(key, value);
    }
    public void setString(String key, String value) {
        tagCompound.putString(key, value);
    }
}
