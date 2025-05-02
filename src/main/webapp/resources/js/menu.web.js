/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
var NOME_MENU = 'menu-principal';
var CHAVE_MENUS = 'menus-webpublico';
var CHAVE_AUTOCOMPLETE = 'menus-webpublico-autocomplete';
var HTML_GERADO = 'html-gerado';

function limparDependenciasMenu() {
    sessionStorage.setItem(CHAVE_MENUS, null);
    sessionStorage.setItem(CHAVE_AUTOCOMPLETE, null);
    sessionStorage.setItem(HTML_GERADO, null);
}

menusAutoComplete = [];

function insereDiv(pai, div) {
    var divPrincipal = pai;

    var ultimoIndice = divPrincipal.children.length - 1;

    var ultimo = divPrincipal.children[ultimoIndice];
    if (ultimo == null) {
        try {
            divPrincipal.insertBefore(div, divPrincipal.children[ultimoIndice]);
        } catch (err) {
            divPrincipal.appendChild(div);
        }
    } else {
        for (var i = 0; i < divPrincipal.children.length; i++) {
            var elemDaVez = divPrincipal.children[i];
            var ordemDaVez = elemDaVez.getAttribute("ordem");
            var ordemProx = null;
            try {
                ordemProx = divPrincipal.children[i + 1];
                ordemProx = ordemProx.getAttribute("ordem");
            } catch (err) {
                ordemProx = divPrincipal.children[i];
                ordemProx = ordemProx.getAttribute("ordem");
            }

            var ordemEmQuestao = div.getAttribute("ordem");

            ordemEmQuestao = parseInt(ordemEmQuestao);
            ordemDaVez = parseInt(ordemDaVez);
            ordemProx = parseInt(ordemProx);

            if (ordemDaVez == ordemProx) {
                if (ordemEmQuestao > ordemDaVez) {
                    try {
                        divPrincipal.insertBefore(div, divPrincipal.children[i + 1]);
                    } catch (err) {
                        divPrincipal.appendChild(div);
                    }
                    break;
                } else {
                    divPrincipal.insertBefore(div, divPrincipal.children[i]);
                    break;
                }
            }

            if (ordemEmQuestao < ordemDaVez) {
                divPrincipal.insertBefore(div, divPrincipal.children[i]);
                break;
            }
        }
    }
}

$(document).ready(function () {

    if (sessionStorage.getItem('ultima-pagina') === 'login') {
        $.cookie("dd-wrapper-0", null, null);
        sessionStorage.setItem('ultima-pagina', null);
    }

    initMenu();

    menusAutoComplete = sessionStorage.getItem(CHAVE_AUTOCOMPLETE);

    menusAutoComplete = JSON.parse(menusAutoComplete);

    $(function () {
        var accentMap = {
            "á": "a",
            "â": "a",
            "à": "a",
            "ã": "a",
            "é": "e",
            "ê": "e",
            "è": "e",
            "í": "i",
            "ì": "i",
            "î": "i",
            "ó": "o",
            "õ": "o",
            "ô": "o",
            "ö": "o",
            "ò": "o",
            "ú": "u",
            "ù": "u",
            "ü": "u",
            "û": "u",
            "ç": "c"
        };

        var normalize = function (term) {
            var ret = "";
            for (var i = 0; i < term.length; i++) {
                ret += accentMap[term.charAt(i)] || term.charAt(i);
            }
            return ret;
        };

        $("#busca").autocomplete({
            source: function (request, response) {
                var matcher = new RegExp($.ui.autocomplete.escapeRegex(request.term), "i");
                var results = ($.grep(menusAutoComplete, function (value) {
                    value = value.label;
                    return matcher.test(value) || matcher.test(normalize(value.toLowerCase()))
                }));
                response(results.slice(0, 10));
            },
            select: function (event, ui) {
                if (ui.item.numeroDeFilhos == null || ui.item.numeroDeFilhos == '0') {
                    fecharMenu();
                }
                $("#busca").val(ui.item.label + " / " + ui.item.id);
                var escolhido = getElement('link' + ui.item.id);
                var inicio = getElement('inicio');
                var clazz = escolhido.getAttribute('class');
                clazz += ' active';

                if (inicio != null) {
                    escolhido.setAttribute('class', clazz);
                    inicio.click();
                }
                saiClicando(ui.item);
            }
        }).data("autocomplete")._renderItem = function (ul, item) {
            return $("<li></li>")
                .data("item.autocomplete", item)
                .append("<a class='tooltip'>" + item.label + "<span>" + item.caminhoDoMenu + "</span></a>")
                .appendTo(ul);
        };
    });
    setTimeout(recuperaEstadoDoMenu, 1000);
});

function saiClicando(item) {
    if (item.paiId != null) {
        for (var i = 0; i < menusAutoComplete.length; i++) {
            if (menusAutoComplete[i].id == item.paiId) {
                saiClicando(menusAutoComplete[i]);
            }
        }
    }
    var legal = getElement('link' + item.id);
    legal.click();
}

function recuperaEstadoDoMenu() {
    var ultimoClickMenu = sessionStorage.getItem('ultimo-click-menu');
    if (ultimoClickMenu) {
        for (var i = 0; i < menusAutoComplete.length; i++) {
            var menu = menusAutoComplete[i];
            if (menu.id == ultimoClickMenu) {
                if (!menu.caminho) {
                    saiClicando(menu);
                }
                break
            }
        }
    }
}

function getElement(id) {
    return document.getElementById(id);
}

function getElementFrom(id, base) {
    for (var i = 0; i < base.children.length; i++) {
        var elem = base.children[i];
        getElementFrom(id, elem);
        var idNovo = elem.getAttribute('id');
        if (idNovo == id) {
            return elem;
        }
    }
}

function criarElemento(elemento) {
    var elem = document.createElement(elemento);
    return elem;
}

function criarUl(menu) {
    var ul = criarElemento('ul');
    ul.setAttribute('id', 'ul_' + menu.id);
    ul.setAttribute('ordem', menu.ordem);
    return ul;
}

function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}

function onClickMenu(id, idPai, descricao) {
    let menuEstaAbero = document.getElementById("mostra-menu").value;
    if (menuEstaAbero && menuEstaAbero === 'true') {
        cleanSessionGenericSearch();
    }
    if (idPai) {
        sessionStorage.setItem('ultimo-click-menu', idPai);
    }
    if (descricao) {
        $('#ul_' + id + ' > li > .stack-menu__link--back').html(descricao);
    }
}

function criarLia(menu) {
    var li = criarElemento('li');
    var a = criarElemento('a');
    li.setAttribute('id', 'li_' + menu.id);
    li.setAttribute('ordem', menu.ordem);
    a.setAttribute('id', 'link' + menu.id);
    a.setAttribute('ordem', 0);
    a.setAttribute('onclick', "onClickMenu(" + menu.id + "," + menu.paiId + ",'" + menu.label + "');");
    if (menu.caminho != null && endsWith(menu.caminho, '.xhtml')) {
        menu.caminho = '/faces' + menu.caminho;
    }
    a.setAttribute('href', getRequestContextPath() + menu.caminho);
    if (menu.icone) {
        a.innerHTML = "<i class=\"fa fa-" + menu.icone + "\"> </i>&nbsp;" + menu.label;
    } else {
        a.innerHTML = "&nbsp;&nbsp;&nbsp;" + menu.label;
    }

    li.appendChild(a);
    return li;
}

function criarElementoAdequado(menu) {
    if (getElement('li_' + menu.id) != null) {
        return getElement('li_' + menu.id);
    }

    var lia = criarLia(menu);
    if (menu.numeroDeFilhos == null || menu.numeroDeFilhos == 0) {
        return lia;
    } else {
        var ul = criarUl(menu);
        insereDiv(lia, ul);
    }
    return lia;
}

function adicionarMenuParaItemDoAutoComplete(menu) {
    var achou = false;

    for (var i = 0; i < menusAutoComplete.length; i++) {
        var menuDaVez = menusAutoComplete[i];
        if (menuDaVez.id == menu.id) {
            achou = true;
            break;
        }
    }

    if (achou == false) {
        menusAutoComplete.push(menu);
    }
}

function criarEstruturaVinculada(menu, filho) {
    adicionarMenuParaItemDoAutoComplete(menu);
    var elem = null;
    elem = criarElementoAdequado(menu);
    if (filho != null) {
        var compPai = getElement('ul_' + menu.id);
        if (compPai == null) {
            compPai = getElementFrom('ul_' + menu.id, elem);
        }
        insereDiv(compPai, filho);
    }

    if (menu.pai == null) {
        var inicio = getElement(NOME_MENU);
        insereDiv(inicio, elem);
    } else {
        criarEstruturaVinculada(menu.pai, elem);
    }
}

function montarMenu(menus) {
    for (var i = 0; i < menus.length; i++) {
        var menu = menus[i];
        criarEstruturaVinculada(menu, null);
    }
    menusAutoComplete = JSON.stringify(menusAutoComplete);
    sessionStorage.setItem(CHAVE_AUTOCOMPLETE, menusAutoComplete);
}

function initMenu() {
    var url = getRequestContextPath() + '/spring/ler-menu/';

    var htmlMenu = sessionStorage.getItem(HTML_GERADO);
    var divMenu = getElement('container-menu');

    if (htmlMenu == 'null' || htmlMenu == null || htmlMenu == 'undefined') {
        var menus = httpGet(url);
        menus = JSON.parse(menus);
        var ul = criarElemento('ul');
        ul.setAttribute('id', NOME_MENU);
        divMenu.appendChild(ul);
        montarMenu(menus);
        htmlMenu = divMenu.innerHTML;
        sessionStorage.setItem(HTML_GERADO, htmlMenu);
    }

    divMenu.innerHTML = htmlMenu;
    $(getElement('container-menu')).stackMenu();
    let innerHeight = window.innerHeight;
    if (innerHeight < 600) {
        $("#container-menu").css({height: innerHeight * 0.5 + 'px'});
    } else if (innerHeight < 700) {
        $("#container-menu").css({height: innerHeight * 0.6 + 'px'});
    } else if (innerHeight < 800) {
        $("#container-menu").css({height: innerHeight * 0.7 + 'px'});
    } else {
        $("#container-menu").css({height: innerHeight * 0.8 + 'px'});
    }
}

function httpGet(theUrl) {
    var xmlHttp = null;
    xmlHttp = new XMLHttpRequest();
    xmlHttp.open("GET", theUrl, false);
    xmlHttp.send(null);
    return xmlHttp.responseText;
}
