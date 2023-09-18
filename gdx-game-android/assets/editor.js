function setupEditor() {
    defineModes()

    const editor = document.querySelector('#editor')
    const codeEditor = CodeMirror.fromTextArea(editor, {
        lineNumbers: true,
		styleActiveLine: true,
        theme: 'js64',
        keyMap: 'sublime',
        mode: 'disable',
        fixedGutter: false
    })

//    codeEditor.setOption('styleActiveLine', { nonEmpty: false })
    
    codeEditor.on('change', () => {
        if (currentFile) {
            app.writeFileContent(currentFile, codeEditor.getValue())
        }
    })

    return codeEditor
}

function defineModes() {
    CodeMirror.defineSimpleMode("JS64", {
        start: [
            { regex: /"(?:[^\\]|\\.)*?(?:"|$)/, token: 'string' },
            { regex: /'(?:[^\\]|\\.)*?(?:'|$)/, token: 'string' },

            { regex: /(#\s*)(TODO:)(.*)/, token: 'todo' },
            { regex: /(\/\/\s*)(TODO:)(.*)/, token: 'todo' },
            { regex: /(#\s*)(DEBUG:)(.*)/, token: 'debug' },
            { regex: /(\/\/\s*)(DEBUG:)(.*)/, token: 'debug' },
            { regex: /#.*/, token: 'comment' },
            { regex: /\/\/.*/, token: 'comment' },

            { regex: /(?:export|if|else|function|return|while|do|for|in|of|this|const|var|new)\b/, token: 'keyword' },

            { regex: /(\.)(\s*)([A-z0-9_]+)/, token: ['operator-2', null, 'variable-3'] },

            { regex: /(\(|\)|\[|\]|\{|\})/, token: "operator" },

            { regex: /[-+\/*=<>!]+/, token: "operator" },

            { regex: /[,\.;]+/, token: "operator-2" },

            { regex: /true|false|null|undefined|VECTOR2|COLOR|STRING|FLOAT|INTEGER|GAME_OBJECT|MathUtils|Vector2|GlyphLayout|Rectangle|Color|Assets|Component|Logger|Scene|Group|Tag|Collision|Input/, token: "atom" },

            {
                regex: /0x[a-f\d]+|[-+]?(?:\.\d+|\d+\.?\d*)(?:e[-+]?\d+)?/i,
                token: "number"
			},

            { regex: /(\$[A-z0-9_]+)/, token: 'variable-2' },


            { regex: /([A-z0-9_]+)(\s*)(\:\:)(\s*)([A-z0-9_]+)/, token: ['type', null, 'operator', null, 'variable-2'] },

		],

        meta: {
            dontIndentStates: ["comment"],
            lineComment: "#"
        }
    });

    CodeMirror.defineSimpleMode("disable", { start: [], meta: {} })
}
