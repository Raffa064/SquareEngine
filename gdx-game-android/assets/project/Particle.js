export Particle::duration = FLOAT

Particle::ready() {
	$time = 0
	$tex = Assets.texture('project/square.png')
	$transform = $obj.get('Transform2D')
	$initialScale = $transform.scale.cpy()
	
	Group.add($obj, 'particles')
}

Particle::process(delta) {
	$time += delta
	
	$transform.rotation += delta * 360
	
	var s = 1-($time/$duration)
	$transform.scale.set($initialScale).scl(s)
	
	if ($time > $duration) {
		$obj.queueFree()
	}
}

Particle::exit() {
	Group.remove($obj, 'particles')
}
