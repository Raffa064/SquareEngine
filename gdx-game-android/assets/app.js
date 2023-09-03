const FILE_TEMPLATE = '<li class="file"><span onclick="{onclick}" class="{type} {fold}">{name}</span><ul class="children">{children}</ul></li>'

var currentFile = app.getEditorData("currentFile", null)


const info = document.querySelector('#info')
const aside = document.querySelector('aside')
const fileExplorer = document.querySelector('#explorer')

if (currentFile != null) {
	openFile(currentFile)
}

aside.style.width = app.getEditorData("asideWidth", "0px")

const currentFolder = app.getFolderPath()
const folder = JSON.parse(app.getFolderContent(currentFolder).replace('\/', '/'))
explorer.innerHTML = convertFileIntoHTML(folder, true)

codeEditor.on('change', () => {
	if (currentFile) {
		app.writeFileContent(currentFile, codeEditor.getValue())	
	}
})

function openFile(path) {
	const extension = path.substring(Math.max(0, path.lastIndexOf('.'))+1, path.length);
	
	switch (extension) {
		case 'obj': 
		case 'scn': 
		case 'cfg': 
			codeEditor.setOption('mode', 'javascript') // JSON files
			break;
		case 'js':
			codeEditor.setOption('mode', 'JS64')
			break;
		case 'md': 
			codeEditor.setOption('mode', 'markdown')
			break;
		case 'png':
		case 'jpg':
		case 'mp3':
		case 'mp4':
		case 'gif':
		case 'apk':
		case 'exe':
		case 'zip':
		case 'bin':
		case 'tar':
		case 'wav':
		case 'ogg':
		case 'opus':
		case 'jpeg':
		case 'bmp':
			return // Binary files (can't open)
		default:
			codeEditor.setOption('mode', 'disable')
			break;
	}
	
	const len = Math.floor((info.getBoundingClientRect().width / 1.5) / 20)
	info.innerText = 'Current file: ...' + path.substring(Math.max(0, path.length - len), path.length)
	currentFile = path
	codeEditor.setValue(app.getFileContent(path))
	
	app.setEditorData("currentFile", currentFile)
}

function toggleFolder(folderElt) {
	folderElt.onclick = () => {
		folderElt.classList.toggle('fold')
	}
}

function convertFileIntoHTML(file, unfold=false) {
	var childrenHTML = ''
	
	if (file.isDirectory) {
		childrenHTML = file.children.reduce((acc, file) => {
			return acc += convertFileIntoHTML(file)
		}, '')
	}
	
	return FILE_TEMPLATE
		.replace('{path}', file.path)
		.replace('{onclick}', file.isDirectory? 'toggleFolder(this)' : "openFile('"+file.path+"')")
		.replace('{type}', file.isDirectory? 'directory' : file.extension)
		.replace('{fold}', unfold? '' : 'fold')
		.replace('{name}', file.isDirectory? file.name + '/' : file.name)
		.replace('{children}', childrenHTML)
}
