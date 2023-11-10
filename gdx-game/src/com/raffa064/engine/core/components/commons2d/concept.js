Comp::ready() {
	$life = 10
	$hitbox = Component.create(_RectangularTrigger)	

	$hitbox.subscribe("enter", $onEnter)
	$hitbox.subscribe("exit", $onExit)
}


Comp::onEnter(obj, hitBox) {
	// On Enter
}

Comp::onExit(obj, hitBox) {
	// On Enter
}

Comp::process(delta) {
	var collided = $hitbox.getCollided()
	
	for (var index in collided) {
		if (collided.obj.group.contains("damage")) {
			$life--
		}
	}
}

Comp::exit() {
	$hitbox.unsubscribe("enter", $onEnter)
	$hitbox.unsubscribe("exit", $onExit)
}
