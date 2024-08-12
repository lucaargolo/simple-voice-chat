package de.maxhenkel.voicechat.plugins.impl;

import de.maxhenkel.voicechat.api.Position;
import net.minecraft.util.Vec3;

import java.util.Objects;

public class PositionImpl implements Position {

    private final Vec3 position;

    public PositionImpl(Vec3 position) {
        this.position = position;
    }

    public PositionImpl(double x, double y, double z) {
        this.position = new Vec3(x, y, z);
    }

    @Override
    public double getX() {
        return position.xCoord;
    }

    @Override
    public double getY() {
        return position.yCoord;
    }

    @Override
    public double getZ() {
        return position.zCoord;
    }

    public Vec3 getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        PositionImpl position1 = (PositionImpl) object;
        return Objects.equals(position, position1.position);
    }

    @Override
    public int hashCode() {
        return position != null ? position.hashCode() : 0;
    }
}
