$(document).ready(function () {
    if (window.WebSocket) {
        var new_uri = window.location.protocol + "//"
            + window.location.hostname
            + (!window.location.port ? "" : ":" + window.location.port);

        console.log("Conectando ao http::", new_uri);
        new_uri = new_uri.replace("https", "wss");
        new_uri = new_uri.replace("http", "ws");
        new_uri = new_uri + (new_uri.endsWith("/") ? "" : "/") + 'push';
        console.log("Conectando ao websocket::", new_uri);

        var ws = new WebSocket(new_uri);
        ws.onmessage = function (event) {
            var text = event.data;
            console.info("Comunicação via websocket::", text);
            handleMessage(text);
        };
    } else {
        console.log("Não foi possível conectar no websocket")
    }
});

function handleMessage(facesmessage) {
    console.log(facesmessage);
    console.log(facesmessage);
    if (facesmessage == 'trocouUnidade') {
        $('#msgAoUsuarioByServer').html("Você efetuou a troca da Unidade Organizacional em que estava logado, é necessário recarregar a página para continuar")
        $('#trocouUnidade').modal({
            backdrop: 'static',
            keyboard: false
        }).modal('show').on('hide', function () {
            location.reload();
        });
    }
    if (facesmessage == 'recarregarPagina') {
        $('#msgAoUsuarioByServer').html("As permissões de acesso do seu usuário foram alteradas, é necessário sair e entrar novamente da aplicação para os ajustem serem aplicados")
        $('#trocouUnidade').modal({
            backdrop: 'static',
            keyboard: false
        }).modal('show').on('hide', function () {
            window.location.href = '/logout?no_session';
            limparDependenciasMenu();
        });
    }
    if (facesmessage == 'memorando') {
        rcAtualizarMemorandos();
    }
    if (facesmessage == 'imprimirRelatorio') {
        rcAtualizarRelatoriosUsuario();
    }
    if (facesmessage == 'logout') {
        window.location.href = '/logout?no_session';
    }
    if (facesmessage == 'mensagemUsuarioBloqueado') {
        window.location.href = '/home';
    }
    if (facesmessage == 'mensagemUsuario') {
        rcAtualizarPainelMesagemUsuario();
    }
    if (facesmessage == 'avisoFimSessao') {
        $('#modalIdleMonitor').on('hidden', function () {
            keepAlive();
            $('.icone-status-usuario').removeClass("vermelho-escuro");
            $('.icone-status-usuario').removeClass("fa-exclamation-circle");
            $('.icone-status-usuario').addClass("verde");
            $('.icone-status-usuario').addClass("fa-check-circle-o");
        })
        $('#modalIdleMonitor').modal('show');
        $('.icone-status-usuario').removeClass("verde");
        $('.icone-status-usuario').removeClass("fa-check-circle-o");
        $('.icone-status-usuario').addClass("vermelho-escuro");
        $('.icone-status-usuario').addClass("fa-exclamation-circle");
    }
}
