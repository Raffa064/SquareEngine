function setupInfo() {	
	const info = document.querySelector('#info')
	
	info.showCurrentFile = (currentFile) => {
		const lastBarIndex = currentFile.lastIndexOf('/')
		
		if (lastBarIndex < 0) lastBarIndex = currentFile.length
		
		const path = currentFile.substring(0, lastBarIndex)
		const name = currentFile.substring(lastBarIndex+1, currentFile.length)
		
		info.innerHTML = '<span><strong>Path:</strong> '+path+'</span><span><strong>File:</strong> '+name+'</span>'
	}
	
	return info
}
