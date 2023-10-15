Teste::func() {
	$prop = "teste"
}

export Outra::teste = FLOAT

Outra::process() {
	
}
Outra::process1() {
	
}
Outra::process2() {
	
}
Outra::process3() {
	
}

Outra::process4() {
	
}

Outro::anything() {}

export Teste::prop0 = STRING
export Teste::prop1 = STRING
export Teste::prop2 = STRING
export Teste::prop3 = STRING

var instance = new Teste()
instance.func()

logger.log(instance.prop)
