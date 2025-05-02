$(document).ready(function () {
    carregarNotificacoesDoResource();
});

function carregarNotificacoesDoResource() {
    try {
        const xmlHttp = new XMLHttpRequest();
        xmlHttp.open("GET", getRequestContextPath() + '/spring/usuario-sistema/usuario-logado', true);
        xmlHttp.timeout = 3000;
        xmlHttp.ontimeout = function () {
            console.error("Timeout ao buscar notifições do usuário!")
        }
        xmlHttp.onload = (e) => {
            if (xmlHttp.readyState === 4) {
                if (xmlHttp.status === 200) {
                    const usuarioLogado = JSON.parse(xmlHttp.responseText);
                    const xmlHttp2 = new XMLHttpRequest();
                    xmlHttp2.timeout = 3000;
                    xmlHttp2.ontimeout = function () {
                        console.error("Timeout ao buscar notifições do usuário!")
                    }
                    xmlHttp2.open("GET", getUrlNotificacaoService() + '/api/notificacoes?usuario=' + usuarioLogado.id + '&orcamentaria=' + usuarioLogado.idUnidadeOrcLogada + '&administrativa=' + usuarioLogado.idUnidadeAdmLogada, true);

                    xmlHttp2.onload = (e) => {
                        if (xmlHttp2.readyState === 4) {
                            if (xmlHttp2.status === 200) {
                                const notificacoes = JSON.parse(xmlHttp2.responseText);
                                popularTabelaNotificacoes(notificacoes);
                            } else {
                                console.error(xmlHttp2.statusText);
                            }
                        }
                    };
                    xmlHttp2.onerror = (e) => {
                        console.error(xmlHttp2.statusText);
                    };
                    xmlHttp2.send(null);
                } else {
                    console.error("Erro ao buscar notifições do usuário!", xmlHttp.statusText);
                }
            }
        };

        xmlHttp.send(null);

    } catch (error) {
        console.error("Erro ao consultar API de notificacoes", error)
    }
}

function popularTabelaNotificacoes(notificacoes) {

    const xmlHttp = new XMLHttpRequest();
    xmlHttp.open("GET", getRequestContextPath() + '/spring/usuario-sistema/tipos-notificacao', false);
    xmlHttp.send(null);
    const tiposNotificacao = JSON.parse(xmlHttp.responseText);

    $("#notificacoes-conteudo > table").empty();
    for (let i = 0; i < notificacoes.length; i++) {
        const notificacao = notificacoes[i];

        const html =
            '<tr class="notificacao" >' +
            '   <td>' +
            '       <div class="icone-notificacao ' + (notificacao.gravidade === 'ERRO' ? 'danger' : notificacao.gravidade === 'ATENCAO' ? 'war' : 'success') + '">' +
            '           <i class="fa fa-exclamation-circle"' +
            '       </div>' +
            '   </td>' +
            '   <td >' +
            '       <a href="/' + notificacao.url + '" class="desc-notificacao">' + tiposNotificacao[notificacao.tipoNotificacao] + '</a>' +
            // '       <span class="desc-notificacao">' + notificacao.descricao + '</span>' +
            '   </td>' +
            '   <td>' +
            '       <a href="/' + notificacao.url + '" class="quant-notificacao">' + notificacao.titulo + '</a> ' +
            // '       <span class="quant-notificacao">' + notificacao.titulo + '</span> '+
            '   </td>' +
            '</tr>'

        $("#notificacoes-conteudo > table").append(html);
    }
    if (notificacoes.length === 0) {
        $("#icone-notificacao").css('display', 'none');
    } else {
        $("#icone-sino").addClass("icon-animated-bell");
        $("#icone-notificacao").text(notificacoes.length);
    }
}
