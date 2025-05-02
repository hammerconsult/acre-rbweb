// SITGeo-Api mapas
var SITGeo_url_js = 'http://sit.riobranco.ac.gov.br/mapa.js?key=34KEzpj7iTKK654_qdZ0Bn7mHI807ljqgLoiKAA';
var SITGeo_url_mapa = 'http://sit.riobranco.ac.gov.br/mapa_sitgeo?key=34KEzpj7iTKK654_qdZ0Bn7mHI807ljqgLoiKAA';

function SITGeo_mergeObj() {
    var resObj = {};
    for (var i = 0; i < arguments.length; i += 1) {
        var obj = arguments[i], keys = Object.keys(obj);
        for (var j = 0; j < keys.length; j += 1) {
            resObj[keys[j]] = obj[keys[j]];
        }
    }
    return resObj;
}

var SITGeo_objeto_ponte;
window.addEventListener('message', receiveMessage, false);

function receiveMessage(event) {
    var dados = event.data;
    SITGeo_objeto_ponte = dados;
    if (dados.caminho_sitgeo != undefined) {
        SITGeo_mapasDisponiveis[dados.mapa_id_sitgeo][dados.caminho_sitgeo] = SITGeo_mergeObj(SITGeo_mapasDisponiveis[dados.mapa_id_sitgeo][dados.caminho_sitgeo], SITGeo_objeto_ponte);
        SITGeo_clear_array(SITGeo_mapasDisponiveis[dados.mapa_id_sitgeo][dados.caminho_sitgeo]);
        try {
            SITGeo_generic_Callback(dados);
        } catch (error) {
        }
        ;
    }
    ;
}

function SITGeo_clear_array(array) {
    for (var key in array) {
        if (array[key] == 'to_remove_') {
            delete array[key]
        }
    }
};
// inicio inclusão de funcoes
var SITGeo_mapasDisponiveis = [];

function SITGeo_incluir_funcoes(objeto) {
    objeto.descricao = [];
    objeto.camadas = [];
    objeto.camadas.descricao = [];
    objeto.camadas.SITGeo_listar = function () {
        var obj = [];
        obj.funcao = 'SITGeo_listar';
        SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].iframe_padrao.contentWindow.postMessage(obj, SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].url_sitgeo)
    };
    objeto.camadas.descricao.SITGeo_listar = 'Cria uma lista de todas as camadas disponÃ­veis no mapa.';
    objeto.camadas.SITGeo_desativarCamada = function (nome) {
        var obj = [];
        obj.funcao = 'SITGeo_desativarCamada';
        obj.nome = nome;
        SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].iframe_padrao.contentWindow.postMessage(obj, SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].url_sitgeo)
    };
    objeto.camadas.descricao.SITGeo_desativarCamada = 'Desativa a camada informada.';
    objeto.camadas.SITGeo_removerCamada = function (nome) {
        var obj = [];
        obj.funcao = 'SITGeo_removerCamada';
        obj.nome = nome;
        SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].iframe_padrao.contentWindow.postMessage(obj, SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].url_sitgeo)
    };
    objeto.camadas.descricao.SITGeo_removerCamada = 'Remove a camada informada.';
    objeto.camadas.SITGeo_criarCamadaVetorial = function (nome) {
        var obj = [];
        obj.funcao = 'SITGeo_criarCamadaVetorial';
        obj.nome = nome;
        SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].iframe_padrao.contentWindow.postMessage(obj, SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].url_sitgeo)
    };
    objeto.camadas.descricao.SITGeo_criarCamadaVetorial = 'Cria uma nova camada vetorial com nome informado.';
    objeto.camadas.SITGeo_ativarCamada = function (nome) {
        var obj = [];
        obj.funcao = 'SITGeo_ativarCamada';
        obj.nome = nome;
        SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].iframe_padrao.contentWindow.postMessage(obj, SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].url_sitgeo)
    };
    objeto.camadas.descricao.SITGeo_ativarCamada = 'Ativa a camada informada.';
    objeto.camadas.SITGeo_criarMenuCamadas = function () {
        var obj = [];
        obj.funcao = 'SITGeo_criarMenuCamadas';
        SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].iframe_padrao.contentWindow.postMessage(obj, SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].url_sitgeo)
    };
    objeto.camadas.descricao.SITGeo_criarMenuCamadas = 'Cria o menu de controle de camadas sobre o mapa.';
    objeto.estilos = [];
    objeto.estilos.descricao = [];
    objeto.estilos.SITGeo_incluirEstilo = function (nome, objeto) {
        var obj = [];
        obj.funcao = 'SITGeo_incluirEstilo';
        obj.nome = nome;
        obj.objeto = objeto;
        SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].iframe_padrao.contentWindow.postMessage(obj, SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].url_sitgeo)
    };
    objeto.estilos.descricao.SITGeo_incluirEstilo = 'Inclui um novo estilo a lista de estilos';
    objeto.lotes = [];
    objeto.lotes.descricao = [];
    objeto.lotes.SITGeo_centralizarLote = function (iq) {
        var obj = [];
        obj.funcao = 'SITGeo_centralizarLote';
        obj.iq = iq;
        SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].iframe_padrao.contentWindow.postMessage(obj, SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].url_sitgeo)
    };
    objeto.lotes.descricao.SITGeo_centralizarLote = 'centraliza no lote informado';
    objeto.lotes.SITGeo_exibirLote = function (bool, objeto_lote) {
        var obj = [];
        obj.funcao = 'SITGeo_exibirLote';
        obj.bool = bool;
        obj.objeto_lote = objeto_lote;
        SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].iframe_padrao.contentWindow.postMessage(obj, SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].url_sitgeo)
    };
    objeto.lotes.descricao.SITGeo_exibirLote = 'Ativa ou desativa a exibiÃ§Ã£o do vetorial do lote informado.';
    objeto.lotes.SITGeo_removerLote = function (iq) {
        var obj = [];
        obj.funcao = 'SITGeo_removerLote';
        obj.iq = iq;
        SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].iframe_padrao.contentWindow.postMessage(obj, SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].url_sitgeo)
    };
    objeto.lotes.descricao.SITGeo_removerLote = 'Remove um lote da lista.';
    objeto.lotes.SITGeo_legendarLote = function (legenda, iq) {
        var obj = [];
        obj.funcao = 'SITGeo_legendarLote';
        obj.legenda = legenda;
        obj.iq = iq;
        SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].iframe_padrao.contentWindow.postMessage(obj, SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].url_sitgeo)
    };
    objeto.lotes.descricao.SITGeo_legendarLote = 'Inclui legenda em um lote carregado.';
    objeto.lotes.SITGeo_getLote = function (IQ, estilo) {
        var obj = [];
        obj.funcao = 'SITGeo_getLote';
        obj.IQ = IQ;
        obj.estilo = estilo;
        SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].iframe_padrao.contentWindow.postMessage(obj, SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].url_sitgeo)
    };
    objeto.lotes.descricao.SITGeo_getLote = 'Localiza e carrega o lote pela inscriÃ§Ã£o cadastral informada.';
    objeto.pontos = [];
    objeto.pontos.descricao = [];
    objeto.pontos.SITGeo_legendarPonto = function (legenda, nome_ponto, camada) {
        var obj = [];
        obj.funcao = 'SITGeo_legendarPonto';
        obj.legenda = legenda;
        obj.nome_ponto = nome_ponto;
        obj.camada = camada;
        SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].iframe_padrao.contentWindow.postMessage(obj, SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].url_sitgeo)
    };
    objeto.pontos.descricao.SITGeo_legendarPonto = 'Inclui o texto sobre o ponto informado, a camada tambÃ©m deve ser informada, e somente o ponto exibido sobre aquela camada serÃ¡ afetado.';
    objeto.pontos.SITGeo_criarPonto = function (nome) {
        var obj = [];
        obj.funcao = 'SITGeo_criarPonto';
        obj.nome = nome;
        SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].iframe_padrao.contentWindow.postMessage(obj, SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].url_sitgeo)
    };
    objeto.pontos.descricao.SITGeo_criarPonto = 'Cria um novo objeto ponto com nome informado.';
    objeto.pontos.SITGeo_removerPonto = function (nome_ponto, camada) {
        var obj = [];
        obj.funcao = 'SITGeo_removerPonto';
        obj.nome_ponto = nome_ponto;
        obj.camada = camada;
        SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].iframe_padrao.contentWindow.postMessage(obj, SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].url_sitgeo)
    };
    objeto.pontos.descricao.SITGeo_removerPonto = 'Remove um ponto definitivamente da lista e da camada informada.';
    objeto.pontos.SITGeo_capturarPonto = function (nome) {
        var obj = [];
        obj.funcao = 'SITGeo_capturarPonto';
        obj.nome = nome;
        SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].iframe_padrao.contentWindow.postMessage(obj, SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].url_sitgeo)
    };
    objeto.pontos.descricao.SITGeo_capturarPonto = 'Captura as coordenadas do prÃ³ximo ponto apÃ³s o click do mouse.';
    objeto.pontos.SITGeo_exibirPonto = function (bool, objeto_ponto, nome_camada) {
        var obj = [];
        obj.funcao = 'SITGeo_exibirPonto';
        obj.bool = bool;
        obj.objeto_ponto = objeto_ponto;
        obj.nome_camada = nome_camada;
        SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].iframe_padrao.contentWindow.postMessage(obj, SITGeo_mapasDisponiveis[SITGeo_mapasDisponiveis.length - 1].url_sitgeo)
    };
    objeto.pontos.descricao.SITGeo_exibirPonto = 'Exibe/Oculta o ponto no mapa sobre a camada informada. A camada deve ser vetorial.';
}; //fim inclusao funcoes
// inicio obj Criar Novo Mapa

function SITGeo_criarMapa(local) {
    var local_obj = document.getElementById(local);
    var iframe_mapa = document.createElement("iframe");
    var currentLocation = window.location;
    var origem = currentLocation.hostname;

    iframe_mapa.style.width = "100%";
    iframe_mapa.style.height = "350px";
    iframe_mapa.style.background = "rgba(255,255,255, 0)";
    iframe_mapa.style.color = "white";
    iframe_mapa.style.border = "none";
    iframe_mapa.id = 'frame_' + local;
    iframe_mapa.class = 'SITGeo_frame_class';
    iframe_mapa.scrollbars = 'NO';

    local_obj.appendChild(iframe_mapa);
    var novo_mapa = {};
    novo_mapa.localizacao = local;
    novo_mapa.iframe_padrao = iframe_mapa;
    novo_mapa.origem = origem;
    novo_mapa.url_sitgeo = SITGeo_url_mapa;
    iframe_mapa.contentDocument.location.replace(SITGeo_url_mapa);
    SITGeo_incluir_funcoes(novo_mapa);
    SITGeo_mapasDisponiveis.push(novo_mapa);
    //iframe_mapa.onload = function(){SITGeo_vincular(SITGeo_mapasDisponiveis.length-1)};
    return novo_mapa;
};


// fim obj Criar Novo Mapa
