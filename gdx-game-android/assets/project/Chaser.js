export Chaser::target = STRING

Chaser::ready() {
	$chasedTransform = Tag.find($target).get('Transform2D')
	$transform = $obj.get('Transform2D')
}

Chaser::process(delta) {
	const chasedPos = $chasedTransform.pos
	const pos = $transform.pos
	
	pos.x -= (pos.x - chasedPos.x) * .003
	pos.y -= (pos.y - chasedPos.y) * .003
}

Chaser::exit() {}
