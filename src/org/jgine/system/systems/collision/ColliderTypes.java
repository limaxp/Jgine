package org.jgine.system.systems.collision;

import java.util.function.Supplier;

import org.jgine.system.systems.collision.collider.AxisAlignedBoundingBox;
import org.jgine.system.systems.collision.collider.AxisAlignedBoundingQuad;
import org.jgine.system.systems.collision.collider.BoundingCircle;
import org.jgine.system.systems.collision.collider.BoundingCylinder;
import org.jgine.system.systems.collision.collider.BoundingSphere;
import org.jgine.system.systems.collision.collider.LineCollider;
import org.jgine.system.systems.collision.collider.PlaneCollider;
import org.jgine.system.systems.collision.collider.PolygonCollider;
import org.jgine.utils.registry.Registry;

public class ColliderTypes {

	public static final ColliderType<Collider> NONE = a("none", () -> Collider.NULL);

	public static final ColliderType<PlaneCollider> PLANE = a("plane", PlaneCollider::new);

	public static final ColliderType<BoundingSphere> SPHERE = a("sphere", BoundingSphere::new);

	public static final ColliderType<BoundingCylinder> CYLINDER = a("cylinder", BoundingCylinder::new);

	public static final ColliderType<AxisAlignedBoundingBox> BOX = a("box", AxisAlignedBoundingBox::new);

	public static final ColliderType<LineCollider> LINE = a("line", LineCollider::new);

	public static final ColliderType<BoundingCircle> CIRCLE = a("circle", BoundingCircle::new);

	public static final ColliderType<AxisAlignedBoundingQuad> QUAD = a("quad", AxisAlignedBoundingQuad::new);

	public static final ColliderType<PolygonCollider> POLYGON = a("polygon", PolygonCollider::new);

	public static <T extends Collider> ColliderType<T> a(String name, Supplier<T> supplier) {
		ColliderType<T> type = new ColliderType<T>(name, supplier);
		type.setId(Registry.COLLIDER_TYPES.register(name, type));
		return type;
	}

	public static ColliderType<?> get(String name) {
		return Registry.COLLIDER_TYPES.get(name);
	}

	public static ColliderType<?> get(int id) {
		return Registry.COLLIDER_TYPES.get(id);
	}
}
