
LuaExample = { }

function  LuaExample:new()
	self.__index = self;
end

function LuaExample:onEnable(entity) 	
end

function LuaExample:onDisable() 	
end

function LuaExample:update() 	
	print("hi")
end
  	
function LuaExample:onCollision(data, other, collider, otherColider) 	
end