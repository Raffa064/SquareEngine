// Built-in constants

var PI = 3.14159265
const globals = {}

// Built-in functions

function max(a, b) {
	return a > b ? a : b
}

function min(a, b) {
	return a < b ? a : b
}

function pow(x, y) {
	return Math.pow(x, y)
}

function sqrt(x) {
	return Math.sqrt(x)
}

function lerp(a, b, t) {
	var minValue = min(a, b)
	var maxValue = max(a, b)
	
	return minValue + (maxValue - minValue) * t
}

function toDegrees(rad) {
	return (rad / PI) * 180
}

function toRadians(degrees) {
	return (degrees / 180) * PI
}

function cos(angleRad) {
	return Math.cos(angleRad)
}

function sin(angleRad) {
	return Math.sin(angleRad)
}

function tan(angleRad) {
	return Math.tan(angleRad)
}

function cosDeg(angleDeg) {
	return Math.cos(toDegrees(angle))
}

function sinDeg(angle) {
	return Math.sin(toDegrees(angle))
}

function tanDeg(angle) {
	return Math.tan(toDegrees(angle))
}

function limit(minValue, maxValue, x) {
	return min(maxValue, max(minValue, x))
}

function floor(x) {
	return Math.floor(x)
}

function ceil(x) {
	return Math.ceil(x)
}

function sign(x) {
	return Math.sign(x)
}

function boolToInt(bool) {
	if (bool) return 1
	
	return 0
}

function avg(numbers) {
	if (numbers.length) {
		return numbers.reduce((prev, curr) => prev + curr, 0) / numbers.length
	}
	
	var total = 0
	for (var i = 0; i < numbers.size(); i++) {
		total += numbers.get(i)
	}
	
	return total / numbers.size()
}

function random(a, b) {
	var r = Math.random()
	
	if (a === undefined && b === undefined) {
		return r // Random 0 - 1
	}
	
	if (b === undefined) { 
		return r * a // Random 0 - a
	}
		
	return lerp(a, b, r) // Random a to b
}

function choice(values) {
	if (values.length) {
		var index = floor(random() * values.length)
		return values[index]
	}
	
	var index = floor(random() * values.size())
	return values.get(index)
}
