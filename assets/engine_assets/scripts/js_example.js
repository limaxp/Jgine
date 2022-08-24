
var transformSystem = Java.type("org.jgine.system.systems.transform.TransformSystem").class;

function setEntity(var entity) {
	this.entity = entity
}

function getEntity() {
	return entity;
}

function onEnable() { 

}

function onDisable() { 

}

function update() { 
	print(entity.getSystem(transformSystem)) 
}

function onCollision(var collision) { 
	
}