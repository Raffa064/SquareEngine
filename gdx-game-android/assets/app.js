const Info = setupInfo()
const Explorer = setupExplorer()
const Editor = setupEditor()

var currentFile = app.getEditorData('currentFile', null)

Explorer.onOpenFile = onOpenFile

Explorer.setFolder(app.getFolderPath())

if (currentFile) {
	onOpenFile(currentFile)
}

function onOpenFile(path) {
	if (!app.existsFile(path)) return
	
    const extension = path.substring(Math.max(0, path.lastIndexOf('.')) + 1, path.length);
    
    switch (extension) {
        case 'obj':
        case 'scn':
        case 'font':
        case 'cfg':
            Editor.setOption('mode', 'javascript') // JSON files
            break;
        case 'js':
            Editor.setOption('mode', 'JS64')
            break;
        case 'md':
            Editor.setOption('mode', 'markdown')
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
        case 'otf':
        case 'ttf':
        case 'fnt':
        case 'iso':
        case 'img':
        case 'bmp':
            return // Binary files (can't open)
        default:
            Editor.setOption('mode', 'disable')
            break;
    }
     
    currentFile = path
    Info.showCurrentFile(currentFile)
   
    Editor.setValue(app.getFileContent(path))
    Editor.clearHistory()
    
    app.setEditorData("currentFile", currentFile)
}
