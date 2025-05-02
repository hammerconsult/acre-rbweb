function getElement(id) {
    return document.getElementById(id);
}

function criarElemento(elemento) {
    var elem = document.createElement(elemento);
    return elem;
}

function httpGetAsyncDasMensagens(theUrl, funcao) {
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function () {
        switch (xmlhttp.readyState) {
            case 0 : // UNINITIALIZED
            case 1 : // LOADING
            case 2 : // LOADED
            case 3 : // INTERACTIVE
                break;
            case 4 : // COMPLETED
                funcao(xmlhttp.responseText);
                break;
            default:
                alert("Erro ao buscar informações, atualize a página e tente novamente");
        }
    };
    xmlhttp.open("GET", theUrl, true);
    xmlhttp.setRequestHeader('url', window.location.pathname);
    xmlhttp.send(null);
}


function exibirMensagensJsonString(data) {
    exibirMensagens(data);
}

function exibirMensagensJson(data) {
    if (!data) {
        return;
    }
    data = JSON.parse(data);
    exibirMensagens(data);
}

function exibirMensagens(data) {
    if (!data) {
        return;
    }

    if (data.length == 0) {
        return;
    }

    var divMensagensGeral = getElement('div-info-dialog-mensagens-nao-lidas');
    divMensagensGeral.innerHTML = "";


    if(data !== undefined){
        for (i = 0; i < data.length; i++) {
            let mensagem = data[i];
            var divConteudoMensagem = criarElemento('div');
            divConteudoMensagem.setAttribute('class', data[i].severity == 'fatal' ? 'alert alert-danger' : 'alert alert-' + data[i].severity);

            var divIcone = criarElemento('div');
            divIcone.setAttribute('class', 'ui-message-' + data[i].severity + "-icon");
            divIcone.setAttribute('style', 'margin-right: 10px;');

            var strong = criarElemento('strong');
            strong.setAttribute('style', 'margin-right: 10px;');

            strong.innerHTML = data[i].mensagem.summary;

            var span = criarElemento('span');
            span.innerHTML = data[i].mensagem.detail;

            divConteudoMensagem.appendChild(divIcone);
            divConteudoMensagem.appendChild(strong);
            divConteudoMensagem.appendChild(span);

            divMensagensGeral.appendChild(divConteudoMensagem);
        }
        dialogMensagensNaoLidas.show();
        $("#dialog-mensagens-nao-lidas").css("z-index", "9999999999999");
    }

}

function limparMensagensNaoLidasNoServidor() {
    var url = getRequestContextPath() + '/spring/mensagens/marcar-mensagens-lidas/';
    httpGetAsyncDasMensagens(url, concluirProcessoLeituraMensagens);
}

function concluirProcessoLeituraMensagens(data) {

}

$(document).ready(function () {
    var url = getRequestContextPath() + '/spring/mensagens/buscar-mensagens-nao-lidas/';
    httpGetAsyncDasMensagens(url, exibirMensagensJson);
});
