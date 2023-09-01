MoveComponent::ready() {
	Scene.setBackground(new Color(0, 0.8, 1, 0))
	
	$rotSpeed = 360 # Per sec
	$transform = $obj.get("Transform2D")
	$speed = 10
	$dirX = 1
	$dirY = 1
	$transform.scale.set(0.2, 0.2)
	
	Tag.use($obj, "Player")
}

MoveComponent::process(delta) {
	$transform.pos.x += $speed * $dirX
	$transform.pos.y += $speed * $dirY
	
	# Scene.camera.position.set($transform.pos.x, $transform.pos.y, 0)
	
	if ($transform.pos.x < 0) {
		$transform.pos.x = 0
		$dirX = 1
	}
		
	if ($transform.pos.x > 1024) {
		$transform.pos.x = 1024
		$dirX = -1
	}
	
	if ($transform.pos.y < 0) {
		$transform.pos.y = 0
		$dirY = 1
	}
		
	if ($transform.pos.y > 600) {
		$transform.pos.y = 600
		$dirY = -1
	}
	
	$transform.rotation += $rotSpeed * delta
}

MoveComponent::exit() {
	
}
