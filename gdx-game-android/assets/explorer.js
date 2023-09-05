function setupExplorer() {
    const Explorer = {}

    const explorer = document.querySelector('#explorer')
    const explorerList = document.querySelector('#explorer-list')
    const resizer = document.querySelector('#explorer .resizer')

    setupExplorerResizer(explorer, resizer)

    Explorer.setFolder = (path) => {
		loadFileList(Explorer, explorerList, path)
    }

    return Explorer
}

function loadFileList(Explorer, explorerList, path, state = []) {
        const folder = JSON.parse(app.getFolderContent(path).replace('\/', '/'))
    	
		const createFileElement = (file) => {
        const fileItem = document.createElement('li')
        const fileName = document.createElement('span')

        fileName.classList.add('name')
        fileName.innerText = file.name
        fileItem.appendChild(fileName)

        if (file.isDirectory) {
            fileItem.classList.add('folder')

            if (state.indexOf(file.path) < 0) {
                fileItem.classList.add('fold')
            }

            const fileAddChild = document.createElement('button')
            const fileChildren = document.createElement('ul')

            fileAddChild.innerText = '+'
            fileAddChild.onclick = () => {
                const createFile = document.createElement('li')
                const createFileIcon = document.createElement('span')
                const createFileName = document.createElement('input')

                createFile.classList.add('create-file')
                createFileIcon.classList.add('icon')
                createFileName.classList.add('name')

                createFileName.oninput = () => {
                    if (createFileName.value.endsWith('/')) {
                        createFileIcon.className = 'icon directory'
                        return
                    }

                    const name = createFileName.value
                    const extension = name.substring(Math.max(0, name.lastIndexOf('.')) + 1, name.length);
                    createFileIcon.className = 'icon ' + extension
                }

                const reloadFolderContent = () => {
                    if (createFileName.value.endsWith('/')) {
                        app.createFolder(file.path + '/' + createFileName.value)
                    } else {
                        app.createFile(file.path + '/' + createFileName.value)
                    }
	
                    loadFileList(Explorer, explorerList, path, state)
                }

                createFileName.addEventListener('change', reloadFolderContent)
                createFileName.addEventListener('focusout', reloadFolderContent)

                createFile.appendChild(createFileIcon)
                createFile.appendChild(createFileName)

                fileChildren.insertBefore(createFile, fileChildren.firstChild)

                createFileName.focus()
            }

            fileChildren.classList.add('children')
            for (const f of file.children) {
                fileChildren.appendChild(createFileElement(f))
            }

            fileName.onclick = () => {
                const fold = fileItem.classList.toggle('fold')

                if (!fold && state.indexOf(file.path) < 0) {
                    state.push(file.path)
                } else {
                    state = state.filter(path => path !== file.path)
                }
            }

            fileItem.appendChild(fileAddChild)
            fileItem.appendChild(fileChildren)


            return fileItem
        }
		
		
        fileItem.classList.add('file', file.extension || file.name)

        fileName.onclick = () => {
            Explorer.onOpenFile(file.path)
        }

        return fileItem
    }

    explorerList.innerHTML = ''
    
	const root = createFileElement(folder)
    root.classList.remove('fold')
	
    explorerList.appendChild(root)
}

function setupExplorerResizer(explorer, resizer) {
    explorer.style.width = app.getEditorData("explorerWidth", "0px")

    var touchX;
    var touchY;

    resizer.ontouchstart = (event) => {
        event.preventDefault()
        touchX = event.touches[0].clientX
        touchY = event.touches[0].clientY
        resizer.classList.add('active')
    }

    resizer.ontouchmove = (event) => {
        event.preventDefault()
        const rect = explorer.getBoundingClientRect()
        const width = rect.width + (event.touches[0].clientX - touchX)

        explorer.style.width = width + "px"
        app.setEditorData("explorerWidth", explorer.style.width)

        touchX = event.touches[0].clientX
        touchY = event.touches[0].clientY
    }

    resizer.ontouchend = () => {
        event.preventDefault()
        resizer.classList.remove('active')
    }
}
