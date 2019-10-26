package entities;

import org.lwjgl.util.vector.Vector3f;

public interface Camera {
	public void move();
    public Vector3f getPosition();
    public float getPitch();
    public float getYaw();
    public float getRoll();
}
