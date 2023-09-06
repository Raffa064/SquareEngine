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

    const createEditableFile = (initialName, updateHandler) => {
        const editableFile = document.createElement('li')
        const editableFileIcon = document.createElement('span')
        const editableFileName = document.createElement('input')

        editableFile.classList.add('editable-file')
        editableFileIcon.classList.add('icon')
        editableFileName.classList.add('name')

        editableFileName.oninput = () => {
            if (editableFileName.value.endsWith('/')) {
                editableFileIcon.className = 'icon directory'
                return
            }

            const name = editableFileName.value
            const extension = name.substring(Math.max(0, name.lastIndexOf('.')) + 1, name.length);
            editableFileIcon.className = 'icon ' + extension
        }

        const reloadFolderContent = () => {
            updateHandler(editableFileName.value)

            loadFileList(Explorer, explorerList, path, state)
        }

        editableFileName.addEventListener('change', reloadFolderContent)
        editableFileName.addEventListener('focusout', reloadFolderContent)

        editableFile.appendChild(editableFileIcon)
        editableFile.appendChild(editableFileName)

        editableFileName.value = initialName
        
		editableFile.requestFocus = () => {
			editableFileName.focus()
        }
		
        return editableFile
    }

    const createFileElement = (file) => {
        const fileItem = document.createElement('li')
        const fileName = document.createElement('span')

        fileName.classList.add('name')
        fileName.innerText = file.name
        fileItem.appendChild(fileName)

        var holdTimeout, doubleClickTimeout, waitingForDoubleClick;

        fileName.ontouchstart = (event) => {
            holdTimeout = setTimeout(() => {
                holdTimeout = null

                app.deleteFile(file.path)
            	loadFileList(Explorer, explorerList, path, state)

            }, 500)

            if (waitingForDoubleClick) {
                waitingForDoubleClick = false

                const updateHandler = (name) => {
                    app.renameFile(file.path, name)
                }
                
                const editableFile = createEditableFile(fileName.innerText, updateHandler) 
                fileItem.replaceWith(editableFile)
				editableFile.requestFocus()
				
                return
            }

            waitingForDoubleClick = true
            setTimeout(() => {
                waitingForDoubleClick = false
            }, 200)
        }
		
		fileName.ontouchmove = (event) => {
            clearTimeout(holdTimeout)
        }

        fileName.ontouchend = (event) => {
            clearTimeout(holdTimeout)
        }

        if (file.isDirectory) {
            fileItem.classList.add('folder')

            if (state.indexOf(file.path) < 0) {
                fileItem.classList.add('fold')
            }

            const fileAddChild = document.createElement('button')
            const fileChildren = document.createElement('ul')

            fileAddChild.innerText = '+'
            fileAddChild.onclick = () => {
                const updateHandler = (name) => {
                    if (name.endsWith('/')) {
                        app.createFolder(file.path + '/' + name)
                    } else {
                        app.createFile(file.path + '/' + name)
                    }
                }
                
                const editableFile = createEditableFile('', updateHandler)
                fileChildren.insertBefore(editableFile, fileChildren.firstChild)
				editableFile.requestFocus()
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
