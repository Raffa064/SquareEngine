const resizer = document.querySelector('aside .resizer')
const parent = resizer.parentNode

var touchX;
var touchY

resizer.ontouchstart = (event) => {
	event.preventDefault()
	touchX = event.touches[0].clientX
	touchY = event.touches[0].clientY
	resizer.classList.add('active')
}
    
resizer.ontouchmove = (event) => {
	event.preventDefault()
	const rect = parent.getBoundingClientRect()
    const width = rect.width + (event.touches[0].clientX - touchX)
        
    parent.style.width = width + "px"
	app.setEditorData("asideWidth", parent.style.width)
		
    touchX = event.touches[0].clientX
    touchY = event.touches[0].clientY
}
    
resizer.ontouchend = () => {
	event.preventDefault()
    resizer.classList.remove('active')
}
