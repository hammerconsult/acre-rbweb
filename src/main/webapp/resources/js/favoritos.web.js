$(document).ready(function () {
    carregarFavoritosDoResource();
});

function carregarFavoritosDoResource() {
    const xmlHttp = new XMLHttpRequest();
    xmlHttp.open("GET", getRequestContextPath() + '/spring/usuario-sistema/favoritos-por-usuario', false);
    xmlHttp.send(null);
    const favoritos = JSON.parse(xmlHttp.responseText);
    popularTabelaFavoritos(favoritos);
}

function popularTabelaFavoritos(favoritos) {
    const contextPath = '#{request.contextPath}';
    $("#lista-favoritos-sistema").empty();
    $("#lista-favoritos-sistema").append('<li><a href="#" onclick="favoritos.show();"><i class="fa fa-plus"></i> Adicionar...</a></li>');
    $("#lista-favoritos-sistema").append('<li class="divider"/>');
    $("#lista-favoritos-sistema").append(' <li style="text-decoration: none!important;padding: 3px 20px; display: none"' +
        '                                id="filtroPesquisaFavoritos">' +
        '                                <input placeholder="Pesquisar..." id="inputPesquisaFavoritos" style="width: 90%"' +
        '                                                                class="mantemNoDrop"' +
        '                                                                onkeyup="pesquisaFavoritos()"/>' +
        '                            </li>');
    for (let i = 0; i < favoritos.length; i++) {
        const favorito = favoritos[i];
        const html = ' <li class="itemFavorito" style="list-style: none outside none!important;">' +
            '<a href="' + favorito.url + '">' +
            ' <span title="' + favorito.url + '">' + favorito.nome + '</span>' +
            '</a>' +
            '</li>';
        $("#lista-favoritos-sistema").append(html);
    }

}
