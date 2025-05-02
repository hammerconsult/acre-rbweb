$(document).ready(function () {
    carregarUnidadesDoResource();
});

function carregarUnidadesDoResource() {
    const xmlHttp = new XMLHttpRequest();
 /*   xmlHttp.open("GET", getRequestContextPath() + '/spring/usuario-sistema/administrativa-por-usuario', false);
    xmlHttp.send(null);
    popularTabelaAdministrativas(JSON.parse(xmlHttp.responseText));

    xmlHttp.open("GET", getRequestContextPath() + '/spring/usuario-sistema/orcamentaria-por-usuario', false);
    xmlHttp.send(null);
    popularTabelaOrcamentarias(JSON.parse(xmlHttp.responseText));
*/
    xmlHttp.open("GET", getRequestContextPath() + '/spring/usuario-sistema/usuario-logado', false);
    xmlHttp.send(null);
    const usuarioLogado = JSON.parse(xmlHttp.responseText);
    popularDadosUsuario(usuarioLogado);
}

function popularDadosUsuario(usuario) {
    $("#data-operacao-sistema").append(usuario.dataOperacao);
    $("#input-data-operacao-sistema").val(usuario.dataOperacao);
    $("#nome-usuario-logado").append(usuario.nome);
    $("#nome-resumido-usuario-logado").append(usuario.nomeResumido);
    $("#hierarquia-orc-logada").append(usuario.hierarquiaOrcLogada);
    $("#hierarquia-adm-logada").append(usuario.hierarquiaAdmLogada);
    if (usuario.podeAlterarData) {
        $("#input-data-operacao-sistema").css("display", "block");
        $("#botao-data-operacao-sistema").css("display", "block");
        $("#msg-data-operacao-sistema").css("display", "none");
    }
}

function mudarDataOperacaoSistema() {
    aguarde.show();
    const dataOperacao = $("#input-data-operacao-sistema").val();
    const xmlHttp = new XMLHttpRequest();
    xmlHttp.open("GET", getRequestContextPath() + '/spring/usuario-sistema/trocar-data-sistema?data-operacao=' + dataOperacao, false);
    xmlHttp.send(null);
    location.reload();
}


function popularTabelaAdministrativas(unidades) {
    $("#listaUnidadesAdministrativasUsuario").empty();
    $("#listaUnidadesAdministrativasUsuario").append(criarItemParaFiltro('filtroPesquisaAdministrativa', 'pesquisaUnidadesAdms', 'pesquisaUnidadesAdministrativas()'));
    for (let i = 0; i < unidades.length; i++) {
        const unidade = unidades[i];
        $("#listaUnidadesAdministrativasUsuario").append(criarItemUnidade('itemAdministrativo', 'trocarUnidadeAdministrativaDoUsuario', unidade, 'administrativa', executarRotinasAoTrocarUnidadeAdministrativaDoUsuario));
    }
}

function popularTabelaOrcamentarias(unidades) {
    $("#listaUnidadesOrcamentariasUsuario").empty();
    $("#listaUnidadesOrcamentariasUsuario").append(criarItemParaFiltro('filtroPesquisaOrcamentaria', 'pesquisaUnidadesOrcs', 'pesquisaUnidadesOrcamentarias()'));
    for (let i = 0; i < unidades.length; i++) {
        const unidade = unidades[i];
        $("#listaUnidadesOrcamentariasUsuario").append(criarItemUnidade('itemOrcamentario', 'trocarUnidadeAdministrativaDoUsuario', unidade, 'orcamentaria', executarRotinasAoTrocarUnidadeOrcamentariaDoUsuario));
    }
}

function criarItemUnidade(classe, funcao, unidade, tipo, callback) {
    return ' <li style="list-style: none outside none!important;" class="' + classe + '"> ' +
        '  <a  role="menuitem" onclick="' + funcao + '(' + unidade.subordinadaId + ', \'' + tipo + '\', ' + callback + ')"' +
        ' style="text-decoration: none!important;padding: 3px 20px; cursor: pointer">' +
        unidade.codigo + ' - ' + unidade.descricao +
        '      </a>' +
        '      </li>';
}

function criarItemParaFiltro(idFiltro, idInput, funcationPesquisa) {
    return '<li style="text-decoration: none !important; padding: 3px 20px; display: none" ' +
        '                                    id="' + idFiltro + '"> ' +
        '                                    <input id="' + idInput + '"' +
        '                                           style="width: 90%" ' +
        '                                           placeholder="Pesquisar..." class="mantemNoDrop" ' +
        '                                           onkeyup="' + funcationPesquisa + '"/> ' +
        '                                </li>';
}

function trocarUnidadeAdministrativaDoUsuario(id, unidade, callback) {

    $('#aguardetrocarunidade').modal('show');

    setTimeout(function () {
        const xmlHttp = new XMLHttpRequest();
        xmlHttp.open("GET", getRequestContextPath() + '/spring/usuario-sistema/trocar-' + unidade + '-usuario?id=' + id, false);
        xmlHttp.send(null);
        $('#aguardetrocarunidade').modal('hide');
        callback();
    }, 1000);

}


function pesquisaUnidadesAdministrativas() {
    var searchText = $('#pesquisaUnidadesAdms').val().toLowerCase();

    $('li.itemAdministrativo').filter(function () {
        return !($(this).text().toLowerCase().indexOf(searchText) != -1);
    }).css('display', 'none');

    $('li.itemAdministrativo').filter(function () {
        return ($(this).text().toLowerCase().indexOf(searchText) != -1);
    }).css('display', 'block');
}

function pesquisaUnidadesOrcamentarias() {
    var searchText = $('#pesquisaUnidadesOrcs').val().toLowerCase();

    $('li.itemOrcamentario').filter(function () {
        return !($(this).text().toLowerCase().indexOf(searchText) != -1);
    }).css('display', 'none');

    $('li.itemOrcamentario').filter(function () {
        return ($(this).text().toLowerCase().indexOf(searchText) != -1);
    }).css('display', 'block');
}

