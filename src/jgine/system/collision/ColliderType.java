package jgine.system.collision;

import java.util.function.Supplier;

import jgine.system.collision.collider.AxisAlignedBoundingBox;
import jgine.system.collision.collider.AxisAlignedBoundingQuad;
import jgine.system.collision.collider.CircleCollider;
import jgine.system.collision.collider.CylinderCollider;
import jgine.system.collision.collider.LineCollider;
import jgine.system.collision.collider.PlaneCollider;
import jgine.system.collision.collider.PolygonCollider;
import jgine.system.collision.collider.SphereCollider;
import jgine.utils.registry.Registry;

public class ColliderType<T extends Collider> implements Supplier<T> {

	public static final ColliderType<PlaneCollider> PLANE = as("plane", PlaneCollider::new);
	public static final ColliderType<SphereCollider> SPHERE = as("sphere", SphereCollider::new);
	public static final ColliderType<CylinderCollider> CYLINDER = as("cylinder", CylinderCollider::new);
	public static final ColliderType<AxisAlignedBoundingBox> BOX = as("box", AxisAlignedBoundingBox::new);
	public static final ColliderType<LineCollider> LINE = as("line", LineCollider::new);
	public static final ColliderType<CircleCollider> CIRCLE = as("circle", CircleCollider::new);
	public static final ColliderType<AxisAlignedBoundingQuad> QUAD = as("quad", AxisAlignedBoundingQuad::new);
	public static final ColliderType<PolygonCollider> POLYGON = as("polygon", PolygonCollider::new);

	public static <T extends Collider> ColliderType<T> as(String name, Supplier<T> supplier) {
		ColliderType<T> type = new ColliderType<T>(name, supplier);
		type.id = Registry.COLLIDER.register(name, type);
		return type;
	}

	public final String name;
	private int id;
	private final Supplier<T> supplier;

	public ColliderType(String name, Supplier<T> supplier) {
		this.name = name;
		this.supplier = supplier;
	}

	public int id() {
		return id;
	}

	@Override
	public T get() {
		return supplier.get();
	}
}
