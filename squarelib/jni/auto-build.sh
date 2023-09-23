#!/bin/bash

# Pasta onde os arquivos estão localizados
pasta_atual="./"

# Executa ndk-build
executar_ndk_build() {
    echo "Executando ndk-build..."
    ndk-build
}

# Inicializa a lista de arquivos e os seus tempos de modificação
arquivos=()
tempos=()

# Preenche a lista de arquivos e tempos de modificação
for arquivo in "$pasta_atual"/*; do
    if [ -f "$arquivo" ]; then
        arquivos+=("$arquivo")
        tempos+=($(stat -c %Y "$arquivo"))
    fi
done

# Loop infinito para verificar atualizações
while true; do
    for ((i = 0; i < ${#arquivos[@]}; i++)); do
        tempo_atual=$(stat -c %Y "${arquivos[$i]}")
        if [ "$tempo_atual" -gt "${tempos[$i]}" ]; then
            echo "Arquivo ${arquivos[$i]} foi atualizado. Executando ndk-build..."
            executar_ndk_build
            tempos[$i]=$tempo_atual
        fi
    done
    sleep 1 # Espera 1 segundo antes de verificar novamente
done
