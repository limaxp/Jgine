# Jgine
 
A general purpose java game engine based on a Entity Component System (ECS)
<p>Powered by <a href="https://www.lwjgl.org/">Lightweight Java Game Library 3</a>.
<p>Window, input with <a href="https://www.glfw.org/">GLFW</a>.
<p>Graphics with <a href="https://www.opengl.org/">OpenGL</a>.
<p>Sound with <a href="https://www.openal.org/">OpenAL</a>.

<h2>Overview</h2>

Features:
 - Entity Component System (ECS)
 - Scenes, entity graph
 - 2d and 3d graphics (OpenGL)
 - Sound, SoundEffects (OpenAL)
 - Math library (Vector, Matrix, AxisAligned, Quaternion)
 - Packet based online features
 - A few extra collection classes
 - A lot of other helper classes and systems for game development
 
 Systems:
- Ai
- Camera
- Collision
- graphic2d
- graphic3d
- Input
- Light
- Particle
- Physic
- Script
- Tilemap
- UI

Not implemented yet:
 - ECS optimizations
 - Systems without java objects (Off-Heap Systems)
 - 3d animation system
 
<h2>Getting Started:</h2>

<h3>Create an engine instance and call start() to start game loop:</h3>
<pre>public static void main(String[] args) {
     Engine engine = new Engine();
     engine.start();
}
</pre>

- After creating the instance all internal systems are initialized and ready to use!
- You can also extend Engine class to override some engine behaviour.

<h3>Create a scene and pass the systems to use:</h3> 
<pre>Scene scene = engine.createScene("sceneName", system1, system2, system3, ...);
</pre>

- Use references in engine class to access built in systems. (e.g. <code>Engine.PHYSIC_SYSTEM</code>)
- Or make you own by extending EngineSystem, SystemScene and SystemObject classes!

<h3>Use scene to build entities and add systems to them:</h3>
<pre>Entity entity = new Entity(scene);
PhysicObject object = entity.addSystem(Engine.PHYSIC_SYSTEM, new PhysicObject());
</pre>

- You can even add the same system multiple times! (e.g. multiple Colliders with CollisionSystem) 
- You can also pass location, rotation and scale to the constructor.

<h2>Prefabs</h2>

Prefabs are entity blueprints created from yaml text files with the <code>.prefab</code> extension.
Use the ResourceManager class to load prefabs.

- Prefabs provide full polymorphism by setting other prefabs as parents effectively coping all state from parent to child.
- Set sub entities by setting other prefabs as childs.
- Set up to 64 tags to differentiate better between types of prefabs.
- Or set any data with the data field.
- Predefine a transform for the entity to use.
- Add any amount of systems.

<p>Yaml example:

<pre>parents:
  - parentName1
  - parentName2
  - ...
childs:
  - childName1
  - childName2
  - ...
tags:
  - tag1
  - tag2
  - ...
data:
  dataName1: data
  dataName2: data
  ...:
transform:
  position: xyzValue
  position: [x, y, z]
  position:
    x: xValue
    y: yValue
    z: zValue
  rotation: xyzValue
  rotation: [x, y, z]
  rotation:
    x: xValue
    y: yValue
    z: zValue
  scale: xyzValue
  scale: [x, y, z]
  scale:
    x: xValue
    y: yValue
    z: zValue
systems:
  systemName1:
    systemData
  systemName2:
    systemData
  ...:
</pre>

Then get the prefab and create entities:

<pre>Prefab prefab = Prefab.get("name");
Entity entity = prefab.create(scene);
</pre>

<h2>Systems</h2>

Description comming soon!
