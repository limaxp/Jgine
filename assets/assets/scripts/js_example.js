
//var cameraSystem = Java.type("org.jgine.system.systems.camera.CameraSystem").class;

class JavaScriptBehavour {

	entity;

	constructor(entity) {
		this.entity = entity;
	}

	getEntity() {
		return entity;
	}

	onEnable() {
	}

	onDisable() {
	}

	update() {
		print(entity);
	}

	onCollision(collision) {
	}
}

function create(entity) {
	return new JavaScriptBehavour(entity);
}