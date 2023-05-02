
LuaBehavour = { entity = null }

function  LuaBehavour:new(entity)
	self.entity = entity
end

function LuaBehavour:getEntity() 
	return self.entity
end

function LuaBehavour:onEnable() 	
end

function LuaBehavour:onDisable() 	
end

function LuaBehavour:update() 	
	print(self.entity);
end
  	
function LuaBehavour:onCollision(collision) 	
end


function create(entity)
	return LuaBehavour(entity) 	
end