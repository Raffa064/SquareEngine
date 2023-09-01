export ParticleEmitter::spawnTime = FLOAT
export ParticleEmitter::duration = FLOAT

ParticleEmitter::ready() {
	$timer = 0
	$count = 0
	$transform = $obj.get('Transform2D')
}

ParticleEmitter::process(delta) {
	$timer += delta
	
	if ($timer > $spawnTime) {
		var cTransform = Component.create('Transform2D')
		cTransform.pos.set($transform.pos)
		cTransform.scale.set(0.05, 0.05)
		
		var cImage = Component.create('Image')
		cImage.texturePath = "project/square.png"
		
		var cParticle = Component.create('Particle')
		cParticle.duration = $duration
		
		var cChaser = Component.create('Chaser')
		cChaser.target = "Player"
		
		var particle = Scene.createObject("Particle" + $count++, cTransform, cImage, cParticle, cChaser)
		Scene.addToScene(particle)
		
		$timer = 0
	}
}

ParticleEmitter::exit() {}
