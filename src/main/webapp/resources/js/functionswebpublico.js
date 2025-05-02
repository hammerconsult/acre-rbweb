String.prototype.capitalizeFirstLetter = function () {
    return this.charAt(0).toUpperCase() + this.slice(1);
}

$(document).ready(function () {


    $(".alfaNumerico").keypress(function () {
        var el = $(this);
        setTimeout(function () {
            var text = $(el).val();
            el.val(text.replace(/[\W]/g, ""));
        }, 100);
    });


    controlaTamanhoLista();
    ajustaPaddingConteudo();
    ajustaTopo();
    ajustaAlertaNotificacoes();
    carregarTodasUnidadesMedidasDaTela();

    $("#icone-notificacao")
        .mouseover(function () {
            $(this).find("span").text("mouse over x " + i);
        })

    var status = document.getElementById("mostra-cabecalho").value;
    if (status == 'false') {
        $("#cabecalho").hide();
        $("#topo").hide();
    }
    var status = document.getElementById("mostra-rodape").value;
    if (status == 'false') {
        $("#div-rodape").hide();
        $("#divIniciar").hide();
    }


    $(".maiusculo").keypress(function () {
        var el = $(this);
        setTimeout(function () {
            var text = $(el).val();
            el.val(text.toUpperCase());
        }, 100);
    });

    $(".monetario").click(function () {
        var el = $(this);
        setTimeout(function () {
            var text = $(el).val();
            if (text == 'R$ 0,00') {
                el.val("R$");
            }
        }, 100);
    });

    $(".numero").keypress(function () {
        var el = $(this);
        setTimeout(function () {
            var text = $(el).val();
            el.val(text.replace(/\D/g, ""));
        }, 100);
    });

    upperText();

    function upperText() {
        $(".maiusculo").bind('paste', function (e) {
            var el = $(this);
            setTimeout(function () {
                var text = $(el).val();
                el.val(text.toUpperCase());
            }, 100);
        });
        $(".maiusculo").keypress(function () {
            var el = $(this);
            setTimeout(function () {
                var text = $(el).val();
                el.val(text.toUpperCase());
            }, 100);
        });
    }

    $(".exercicio").mask("9999");

    mostraEscondeFiltrosUnidades();
    mostraEscondeFiltrosFavoritos();
    copiaBreadCrump();
    recarregaPaginaSeUsarBotaoVoltarBrowser();

});


function recarregaPaginaSeUsarBotaoVoltarBrowser() {
    //Trecho faz recarregar a página qndo usado os botões de navegação do browser, voltar e avançar
    window.addEventListener("pageshow", function (event) {
        var historyTraversal = event.persisted ||
            (typeof window.performance != "undefined" &&
                window.performance.navigation.type === 2);
        if (historyTraversal) {
            window.location.reload();
        }
    });
}

function esconteNavegadorCalendar() {
    $('.ui-datepicker-prev').css('display', 'none')
    $('.ui-datepicker-next').css('display', 'none')
}

function mostraNavegadorCalendar() {
    if ($(".ui-datepicker-div").css('display') == "none") {
        $('.ui-datepicker-prev').css('display', 'block');
        $('.ui-datepicker-next').css('display', 'block');
        $(".ui-datepicker-div").css('z-index', '99999999');
    }
}

$(window).load(function () {
    $("#menu").hide();
    $("#menu").width('300px');
    document.getElementById("mostra-menu").value = 'false';

});

function atribuirStyles(elemento, styles) {
    for (var s in styles) {
        elemento.style[s] = styles[s];
    }
}

function redimensionarLadosDialog(id) {

    var info = document.getElementById(id);
    var style = info.style;
    var wTotal = window.innerWidth;
    var diferencaW = wTotal * 0.2;
    style.left = diferencaW / 2 + "px";
    style.right = diferencaW / 2 + "px";
    style.width = (wTotal - diferencaW) + "px";
}

function redimensionarAlturaDialog(id) {

    var info = document.getElementById(id);
    var style = info.style;
    var wTotal = window.innerHeight;
    var diferencaW = wTotal * 0.2;
    style.top = diferencaW / 2 + "px";
    style.bottom = diferencaW / 2 + "px";
    style.height = (wTotal - diferencaW) + "px";
    style.overflow = 'auto';
}

function toggleCabecalho() {
    if (document.getElementById("mostra-cabecalho").value == 'false') {
        document.getElementById("mostra-cabecalho").value = 'true';
    } else {
        document.getElementById("mostra-cabecalho").value = 'false';
    }
    $("#formularioTopo").slideToggle("fast");
    $("#cabecalho").slideToggle("fast");
    $("#topo").slideToggle("fast");
}

function toggleRodape() {
    if (document.getElementById("mostra-rodape").value == 'false') {
        document.getElementById("mostra-rodape").value = 'true';
    } else {
        document.getElementById("mostra-rodape").value = 'false';
    }
    $("#div-rodape").slideToggle("fast");
    $("#divIniciar").slideToggle("fast");
}

// Ouvidor de retorno à pagina
isOuvidorAtivo = false;
$(window).focus(function () {
    if (!isOuvidorAtivo)
        return;
    func();
});

function ativarOuvidor(f) {
    isOuvidorAtivo = true;
    func = f;
}

function desativarOuvidor() {
    isOuvidorAtivo = false;
}

function abrirFecharMenu() {
    if (document.getElementById("mostra-menu").value == 'false') {
        abrirMenu();
    } else {
        fecharMenu();
    }
}

function abrirMenu() {
    var hTotal = window.innerHeight;
    document.getElementById("menu").style.height = ''.concat(hTotal, 'px');
    $('#menu').css('display', 'block');
    $('#menu').removeClass('fadeOutLeft');
    document.getElementById("mostra-menu").value = 'true';
    $(this).toggleClass('pesquisa-menu');
}

function fecharMenu() {
    var menu = document.getElementById("mostra-menu");
    if (menu != null && menu.value == 'true') {
        $('#menu').css('display', 'none');
        $('#menu').addClass('fadeOutLeft');
        document.getElementById("mostra-menu").value = 'false';
    }
}

function setaFoco(arg) {
    if (typeof arg != 'undefined' && document.getElementById(arg)) {
        document.getElementById(arg).focus();
    }
}

function selecionarConteudoDe(arg) {
    document.getElementById(arg).select();
}

function copiaValor(de, para) {
    var conteudo = de.value;
    document.getElementById(para).value = conteudo;
}

function TocarSom(obj) {
    var sound = document.getElementById(obj);
    sound.Play();
}

PrimeFaces.locales['pt_BR'] = {
    closeText: 'Fechar',
    prevText: 'Anterior',
    nextText: 'Próximo',
    currentText: 'Começo',
    monthNames: ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho', 'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'],
    monthNamesShort: ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho', 'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'],
    dayNames: ['Domingo', 'Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado'],
    dayNamesShort: ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'],
    dayNamesMin: ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sab'],
    weekHeader: 'Semana',
    firstDay: 1,
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix: '',
    timeOnlyTitle: 'Horas',
    timeText: 'Tempo',
    hourText: 'Hora',
    minuteText: 'Minuto',
    secondText: 'Segundo',
    currentText: 'Data Atual',
    ampm: false,
    month: 'Mês',
    week: 'Semana',
    day: 'Dia',
    allDayText: 'Todo Dia'
}

function mascaraMonetaria(arg, permiteValorNegativo) {
    permiteValorNegativo = permiteValorNegativo != null ? permiteValorNegativo : true;
    //Set up the NumberMasks
    var separadorDecimal = ",";
    var separadorMilhar = ".";

    // Validação para valor negativo
    if (permiteValorNegativo == false) {
        arg.addEventListener("blur", function () {
            arg.value = arg.value.replace('-', '');
            return;
        });
    }

    var formato = new NumberParser(2, separadorDecimal, separadorMilhar, true);
    formato.currencySymbol = "R$";
    formato.useCurrency = true;
    formato.allowNegative = permiteValorNegativo;
    formato.currencyInside = true;
    if (arg.className.indexOf('mascaraMonetaria') == -1) {
        var mascaraMonetariaFinal = new NumberMask(formato, arg, 20);
        mascaraMonetariaFinal.leftToRight = true;
        arg.setAttribute('autocomplete', 'off');
        arg.className += ' mascaraMonetaria';
    }
}

function mascaraPonto(arg) {
    //Set up the NumberMasks
    var separadorDecimal = ",";
    var separadorMilhar = ".";

    var formato = new NumberParser(2, separadorDecimal, separadorMilhar, true);
    formato.useCurrency = false;
    formato.allowNegative = false;
    formato.currencyInside = true;

    if (arg.className.indexOf('mascaraMonetaria') == -1) {
        var mascaraMonetariaFinal = new NumberMask(formato, arg, 20);
        mascaraMonetariaFinal.leftToRight = true;
        arg.setAttribute('autocomplete', 'off');
        arg.className += ' mascaraMonetaria';
    }
}

function mascaraPonto1Casa(arg) {
    //Set up the NumberMasks
    var separadorDecimal = ",";
    var separadorMilhar = ".";

    var formato = new NumberParser(1, separadorDecimal, separadorMilhar, true);
    formato.useCurrency = false;
    formato.allowNegative = false;
    formato.currencyInside = true;

    if (arg.className.indexOf('mascaraMonetaria') == -1) {
        var mascaraMonetariaFinal = new NumberMask(formato, arg, 20);
        mascaraMonetariaFinal.leftToRight = true;
        arg.setAttribute('autocomplete', 'off');
        arg.className += ' mascaraMonetaria';
    }
}

function mascaraPonto4Casas(arg) {
    //Set up the NumberMasks
    var separadorDecimal = ",";
    var separadorMilhar = ".";

    var formato = new NumberParser(4, separadorDecimal, separadorMilhar, true);
    formato.useCurrency = false;
    formato.allowNegative = false;
    formato.currencyInside = true;

    if (arg.className.indexOf('mascaraMonetaria') == -1) {
        var mascaraMonetariaFinal = new NumberMask(formato, arg, 20);
        mascaraMonetariaFinal.leftToRight = true;
        arg.setAttribute('autocomplete', 'off');
        arg.className += ' mascaraMonetaria';
    }
}

function mascaraPonto3Casas(arg) {
    //Set up the NumberMasks
    var separadorDecimal = ",";
    var separadorMilhar = ".";

    var formato = new NumberParser(3, separadorDecimal, separadorMilhar, true);
    formato.useCurrency = false;
    formato.allowNegative = false;
    formato.currencyInside = true;

    if (arg.className.indexOf('mascaraMonetaria') == -1) {
        var mascaraMonetariaFinal = new NumberMask(formato, arg, 20);
        mascaraMonetariaFinal.leftToRight = true;
        arg.setAttribute('autocomplete', 'off');
        arg.className += ' mascaraMonetaria';
    }
}

function mascara(o, f) {
    v_obj = o
    v_fun = f
    setTimeout("execmascara()", 1)
}

function execmascara() {
    v_obj.value = v_fun(v_obj.value)
}

function mascaraSufixo(o, f, sufixo) {
    v_obj = o
    v_fun = f
    setTimeout("execSufixo(" + sufixo + ")", 1)
}

function execSufixo(sufixo) {
    v_obj.value = v_fun(v_obj.value, sufixo)
}

function porcentagem(v) {
    v = v.replace(/%/, "")
    valor = v

    v = valor + "%"
    return v
}

function sufixo(v, sufixo) {
    v = v.replace(sufixo, "")
    valor = v

    v = valor + sufixo;
    return v
}

function somenteNumeros(event, campo, permiteNegativo, permiteZero, permitePositivo) {
    permiteNegativo = permiteNegativo != null ? permiteNegativo : false;
    permiteZero = permiteZero != null ? permiteZero : false;
    permitePositivo = permitePositivo != null ? permitePositivo : true;

    var tecla = event.which || event.charCode || event.keyCode || 0;

    if (permiteNegativo == true) {
        campo.addEventListener("blur", function () {
            if (campo.value.trim() == '-') {
                campo.value = '';
            }
            if (parseInt(campo.value) == 0) {
                campo.value = 0;
            }
        });
    }

    if (permiteZero == false) {
        campo.addEventListener("blur", function () {
            if (parseInt(campo.value.trim()) == 0) {
                campo.value = '';
            }
        })
    }

    if (permitePositivo == false) {
        campo.addEventListener("blur", function () {
            if (parseInt(campo.value.trim()) > 0) {
                campo.value = '';
            }
        })
    }

    if (tecla == 9) {
        return true;
    }

    if (permiteNegativo && (tecla == 45 || tecla == 109)) {
        var valor = campo.value;
        if (valor.startsWith('-')) {
            valor = valor.replace('-', '');
            campo.value = valor;
            event.preventDefault();
            return;
        }

        if (valor.indexOf('-') <= 0) {
            valor = '-'.concat(valor);
            campo.value = valor;
            event.preventDefault();
            return;
        }
    }

    if (permiteNegativo == false && (tecla == 45 || tecla == 109)) {
        event.preventDefault();
        return;
    }


    if ((tecla > 47 && tecla < 58) || tecla == 45) {
        return true;
    } else {
        if (tecla == 8 || tecla == 0) return true;
        else event.preventDefault();
    }


}

function soNumeros(v) {
    return v.replace(/\D/g, "")
}

function semEspaco(v) {
    v = v.replace(/[\s\t\n]/g, "");
    return v;
}

function virgulaPorPonto(v) {
    return v.replace(",", ".")
}

function soAlfaNumerico(v) {

    return v.replace(/[^a-zA-Z0-9]/g, "")
}

function soSigla(v) {

    return v.replace(/[^a-zA-Z/-]/g, "")
}

function soNomes(v) {

    return v.replace(/[^a-zA-Zà-úÀ-Ú ]/g, "")
}

function monetario(v) {
    return v.replace(/[A-Za-z$-]/g, "")
}

function Data(v) {
    v = v.replace(/\D/g, "")
    v = v.replace(/(\d{2})(\d)/, "$1/$2")
    v = v.replace(/(\d{2})(\d)/, "$1/$2")
    return v

}

function ajustaTopo() {
    var pull = $('#pull');
    var menu = $('nav ul');

    $(pull).on('click', function (e) {
        e.preventDefault();
        menu.slideToggle();
    });

    $(window).resize(function () {
        var w = $(window).width();
        if (w > 320 && menu.is(':hidden')) {
            menu.removeAttr('style');
        }
        ajustaPaddingConteudo();
    });
}

function ajustaPaddingConteudo() {
    var tamanhoTopo = $('#superior').height();
    tamanhoTopo += 10;
    $('#conteudo').css('padding-top', tamanhoTopo + 'px')
}


function getCookie(c_name) {
    var i, x, y, ARRcookies = document.cookie.split(";");
    for (i = 0; i < ARRcookies.length; i++) {
        x = ARRcookies[i].substr(0, ARRcookies[i].indexOf("="));
        y = ARRcookies[i].substr(ARRcookies[i].indexOf("=") + 1);
        x = x.replace(/^\s+|\s+$/g, "");
        if (x == c_name) {
            return unescape(y);
        }
    }
}

function setCookie(c_name, value, exdays) {
    var exdate = new Date();
    exdate.setDate(exdate.getDate() + exdays);
    var c_value = escape(value) + ((exdays == null) ? "" : "; expires=" + exdate.toUTCString());
    document.cookie = c_name + "=" + c_value;
}

function checkCookie() {
    var username = getCookie("username");
    if (username != null && username != "") {
        alert("Welcome again " + username);
    } else {
        username = prompt("Please enter your name:", "");
        if (username != null && username != "") {
            setCookie("username", username, 365);
        }
    }
}

function limitador(field, maxlimit) {
    if (field.value.length > maxlimit) {
        field.value = field.value.substring(0, maxlimit);
    }
}

function mcep(v) {
    v = v.replace(/\D/g, "")                    //Remove tudo o que não é dígito
    v = v.replace(/^(\d{5})(\d)/, "$1-$2")         //Esse é tão fácil que não merece explicações
    return v
}

function mtel(v) {
    v = v.replace(/\D/g, "");             //Remove tudo o que não é dígito
    v = v.replace(/^(\d{2})(\d)/g, "($1) $2"); //Coloca parênteses em volta dos dois primeiros dígitos
    v = v.replace(/(\d)(\d{4})$/, "$1-$2");    //Coloca hífen entre o quarto e o quinto dígitos
    if (v.toString().length > 15) {
        v = v.replace(v, v.toString().substring(0, 14));
    }
    return v;
}

function cnpj(v) {
    v = v.replace(/\D/g, "")                           //Remove tudo o que não é dígito
    v = v.replace(/^(\d{2})(\d)/, "$1.$2")             //Coloca ponto entre o segundo e o terceiro dígitos
    v = v.replace(/^(\d{2})\.(\d{3})(\d)/, "$1.$2.$3") //Coloca ponto entre o quinto e o sexto dígitos
    v = v.replace(/\.(\d{3})(\d)/, ".$1/$2")           //Coloca uma barra entre o oitavo e o nono dígitos
    v = v.replace(/(\d{4})(\d)/, "$1-$2")              //Coloca um hífen depois do bloco de quatro dígitos
    return v
}

function mcpf(v) {
    v = v.replace(/\D/g, "")                    //Remove tudo o que não é dígito
    v = v.replace(/(\d{3})(\d)/, "$1.$2")       //Coloca um ponto entre o terceiro e o quarto dígitos
    v = v.replace(/(\d{3})(\d)/, "$1.$2")       //Coloca um ponto entre o terceiro e o quarto dígitos
    //de novo (para o segundo bloco de números)
    v = v.replace(/(\d{3})(\d{1,2})$/, "$1-$2") //Coloca um hífen entre o terceiro e o quarto dígitos
    return v
}

function mCpfCnpj(v) {
    v = v.replace(/\D/g, "")
    if (v.length <= 13) { //CPF
        v = v.replace(/(\d{3})(\d)/, "$1.$2")
        v = v.replace(/(\d{3})(\d)/, "$1.$2")
        v = v.replace(/(\d{3})(\d{1,2})$/, "$1-$2")
    } else { //CNPJ
        v = v.replace(/^(\d{2})(\d)/, "$1.$2")
        v = v.replace(/^(\d{2})\.(\d{3})(\d)/, "$1.$2.$3")
        v = v.replace(/\.(\d{3})(\d)/, ".$1/$2")
        v = v.replace(/(\d{4})(\d)/, "$1-$2")
    }
    return v
}

function mdata(v) {
    v = v.replace(/\D/g, "");                    //Remove tudo o que não é dígito
    v = v.replace(/(\d{2})(\d)/, "$1/$2");
    v = v.replace(/(\d{2})(\d)/, "$1/$2");

    v = v.replace(/(\d{2})(\d{2})$/, "$1$2");
    if (v.toString().length > 10) {
        v = v.replace(v, v.toString().substring(0, 10));
    }
    return v;
}

function mdataMesAno(v) {
    v = v.replace(/\D/g, "");                    //Remove tudo o que não é dígito
    v = v.replace(/(\d{2})(\d{4})/, "$1/$2");
    if (v.toString().length > 7) {
        v = v.replace(v, v.toString().substring(0, 7));
    }
    if (v.toString().substring(0, 2) > parseInt('12')) {
        v = v.replace(v.toString().substring(0, 2), '12');
    }
    return v;
}

function mtempo(v) {
    v = v.replace(/\D/g, "");                    //Remove tudo o que não é dígito
    v = v.replace(/(\d{1})(\d{2})(\d{2})/, "$1:$2.$3");
    return v;
}

function mhora(v) {
    v = v.replace(/\D/g, "");                    //Remove tudo o que não é dígito
    v = v.replace(/(\d{2})(\d)/, "$1h$2");
    return v;
}

function mhoraminuto(v) {
    v = v.replace(/\D/g, "");
    v = v.replace(/(\d{2})(\d)/, "$1:$2");
    if (v.toString().length > 5) {
        v = v.replace(v, v.toString().substring(0, 5));
    }
    return v;
}

function mrg(v) {
    v = v.replace(/\D/g, "");                                      //Remove tudo o que não é dígito
    v = v.replace(/(\d)(\d{7})$/, "$1.$2");    //Coloca o . antes dos últimos 3 dígitos, e antes do verificador
    v = v.replace(/(\d)(\d{4})$/, "$1.$2");    //Coloca o . antes dos últimos 3 dígitos, e antes do verificador
    v = v.replace(/(\d)(\d)$/, "$1-$2");               //Coloca o - antes do último dígito
    return v;
}

function mnum(v) {
    v = v.replace(/\D/g, "");                                      //Remove tudo o que não é dígito
    return v;
}

function mvalor(v) {
    v = soNumeros(v);
    v = v.replace(/\D/g, "");//Remove tudo o que não é dígito
    v = v.replace(/(\d)(\d{14})$/, "$1.$2");//coloca o ponto dos trilhoes
    v = v.replace(/(\d)(\d{11})$/, "$1.$2");//coloca o ponto dos bilhoes
    v = v.replace(/(\d)(\d{8})$/, "$1.$2");//coloca o ponto dos milhões
    v = v.replace(/(\d)(\d{5})$/, "$1.$2");//coloca o ponto dos milhares

    v = v.replace(/(\d)(\d{2})$/, "$1,$2");//coloca a virgula antes dos 2 últimos dígitos
    if (v.toString().length > 22) {
        v = v.replace(v, v.toString().substring(0, 22));
    }
    return v;
}

function porcentagemSomenteNumero(v) {
    v = soNumeros(v);
    if (parseInt(v.toString()) > parseInt('100')) {
        v = v.replace(v, '100');
    }
    v = porcentagem(v);
    return v;
}

function porcentagemSemSimbolo(v) {
    if (parseInt(v.toString()) > parseInt('100')) {
        v = v.replace(v, '100');
    }
    return v;
}

function mDataHora(v) {
    v = soNumeros(v);

    v = v.replace(/(\d{2})(\d)/, "$1/$2");
    v = v.replace(/(\d{2})(\d)/, "$1/$2");

    v = v.replace(/(\d{2})(\d{2})$/, "$1$2");
    v = v.replace(/(\d{4})(\d{2})/, "$1 $2:");
    v = v.replace(/(\d{2})(\d{2})(\d{4})(\d{2})(\d{2})/, "$1/$2/$3 $4:$5");

    if (v.toString().length > 16) {
        v = v.replace(v, v.toString().substring(0, 16));
    }
    return v;
}

function keyUpLimparCampo(campoOrigem, idCampoDestino) {
    var split = campoOrigem.id.toString().split(':');
    var idOrigem = split[split.length - 1];

    if (campoOrigem.value.toString().trim().length == 0 || campoOrigem.value == '' || campoOrigem.value == 'undefined') {
        var idPanel = campoOrigem.id.replace(idOrigem, idCampoDestino);
        var panel = document.getElementById(idPanel);
        panel.value = '';
    }
}

function keyUpEsconderCampo(campoOrigem, idCampoDestino) {
    var split = campoOrigem.id.toString().split(':');
    var idOrigem = split[split.length - 1];

    if (campoOrigem.value.toString().trim().length == 0 || campoOrigem.value == '' || campoOrigem.value == 'undefined') {
        var idPanel = campoOrigem.id.replace(idOrigem, idCampoDestino);
        var panel = document.getElementById(idPanel);
        panel.style.display = 'none';
    }
}

function keyUpDesabilidarCampo(campoOrigem, idCampoDestino) {
    var split = campoOrigem.id.toString().split(':');
    var idOrigem = split[split.length - 1];

    if (campoOrigem.value.toString().trim().length == 0 || campoOrigem.value == '' || campoOrigem.value == 'undefined') {
        var idPanel = campoOrigem.id.replace(idOrigem, idCampoDestino);
        var panel = document.getElementById(idPanel);
        panel.disabled = true;
    }
}

function keyUpLimparEsconderDesabilidarCampo(campoOrigem, idCampoDestino, limpar, esconder, desabilidar) {
    var split = campoOrigem.id.toString().split(':');
    var idOrigem = split[split.length - 1];

    if (campoOrigem.value.toString().trim().length == 0 || campoOrigem.value == '' || campoOrigem.value == 'undefined') {
        var idPanel = campoOrigem.id.replace(idOrigem, idCampoDestino);
        var panel = document.getElementById(idPanel);
        if (limpar == true) {
            panel.value = '';
        }
        if (esconder == true) {
            panel.style.display = 'none';
        }
        if (desabilidar == true) {
            panel.disabled = true;
        }
    }
}

function keyUpCampoVazio(campoOrigem) {
    if (campoOrigem.value.toString().trim().length == 0 || campoOrigem.value == '' || campoOrigem.value == 'undefined') {
        return true;
    }
}

function mMesAno(v) {
    v = v.replace(/\D/g, "");                    //Remove tudo o que não é dígito
    v = v.replace(/(\d{2})(\d)/, "$1/$2");
    v = v.replace(/(\d{4})(\d)/, "$1/$2");

    v = v.replace(/(\d{2})(\d{4})$/, "$1$2");
    if (v.toString().length > 7) {
        v = v.replace(v, v.toString().substring(0, 7));
    }
    return v;
}

function mostrarComponente(id) {
    var comp = document.getElementById(id);
    comp.style.display = 'block';
}

function ocultarComponente(id) {
    var comp = document.getElementById(id);
    comp.style.display = 'none';
}

function ajustaAlertaNotificacoes() {
    var conteudo = $("#icone-notificacao").html();
    if (conteudo > 0) {
        $("#alerta-notificacoes").css('display', "block");
    } else {
        $("#alerta-notificacoes").css('display', "none");
    }
}

function visualizarChamado() {
    $("#meusChamados").fadeOut();
    $("#divChamado").fadeIn();
    $("#opcoesChamado").css('display', "none");
}

function voltarVisualizarChamados() {
    $("#divChamado").fadeOut();
    $("#meusChamados").fadeIn();
    $("#opcoesChamado").css('display', "block");
}

function notifica() {
    var display = $("#notificacoes").css('display');
    if ($("#notificacoes").is(":hidden")) {
        $('#notificacoes').slideToggle();
        $("#alerta-notificacoes").fadeOut();
    } else {
        $('#notificacoes').slideToggle();
        $("#alerta-notificacoes").fadeIn();
    }
    ajustaAlertaNotificacoes();
};


function mudaTelaNotificacao() {
    var display = $("#notificacoes-conteudo").css('display');
    if (display == 'none') {
        $("#notificacoes-conteudo").css('display', 'block');
        $("#mensagens-conteudo").css('display', 'none');
        $("#iconeMudaTelaNotificacao").removeClass();
        $("#iconeMudaTelaNotificacao").addClass('icon-comment');


    } else {
        $("#notificacoes-conteudo").css('display', 'none');
        $("#mensagens-conteudo").css('display', 'block');
        $("#iconeMudaTelaNotificacao").removeClass();
        $("#iconeMudaTelaNotificacao").addClass('icon-th-list');

    }
};

function mascaraMonetariaDinamica(arg, qtde_casas_milhar, qtde_casas_decimais, cifrao) {
    //Set up the NumberMasks

    var separadorDecimal = ",";
    var separadorMilhar = ".";

    var formato = new NumberParser(qtde_casas_decimais, separadorDecimal, separadorMilhar, true);
    if (cifrao != null && cifrao != false) {
        formato.currencySymbol = "R$";
        formato.useCurrency = true;
        formato.currencyInside = true;
    }
    formato.allowNegative = false;

    if (arg.className.indexOf('mascaraMonetaria') == -1) {
        var mascaraMonetariaFinal = new NumberMask(formato, arg, qtde_casas_milhar);
        mascaraMonetariaFinal.leftToRight = true;
        arg.setAttribute('autocomplete', 'off');
        arg.className += 'mascaraMonetaria';
    }
}

function ajustaImpressaoRelatorio() {
    var wTotal = window.innerWidth;
    var hTotal = window.innerHeight;
    var diferencaW = wTotal * 0.2;
    var diferencaH = hTotal * 0.1;
    var aplicarW = ((diferencaW / 2) * 100) / wTotal;
    var aplicarH = ((diferencaH / 2) * 100) / wTotal;
    $("#relatorio").css("width", (wTotal - diferencaW) + "px");
    $("#relatorio").css("right", aplicarW + "%");
    $("#relatorio").css("left", "inherit");
    $("#relatorio").css("bottom", aplicarH + "%");
    $(".relatorioMedia").css("height", ((hTotal - diferencaH) - 100) + "px");
}

function abrirDialoVersao() {
    ajustaDialogVersao();
    $("#versaoDialog").modal('show');
}

function ajustaDialogVersao() {
    var wTotal = window.innerWidth;
    var hTotal = window.innerHeight;
    var diferencaW = wTotal * 0.3;
    var diferencaH = hTotal * 0.2;
    var aplicarW = ((diferencaW / 2) * 100) / wTotal;
    $("#versaoDialog").css("width", (wTotal - diferencaW) + "px");
    $("#versaoDialog").css("right", aplicarW + "%");
    $("#versaoDialog").css("left", "inherit");
}

function mostraRelatorio() {
    $('#relatorio').modal('show');
}

function mostraDocumentoOficial() {
    $('#documentoOficial').modal('show');
}

function mostraErro500() {
    $('#erro500').modal('show');
}

function ajustaImpressaoDocumentoOficial() {
    var wTotal = window.innerWidth;
    var hTotal = window.innerHeight;
    var diferencaW = wTotal * 0.2;
    var diferencaH = hTotal * 0.1;
    var aplicarW = ((diferencaW / 2) * 100) / wTotal;
    var aplicarH = ((diferencaH / 2) * 100) / wTotal;
    $("#documentoOficial").css("width", (wTotal - diferencaW) + "px");
    $("#documentoOficial").css("right", aplicarW + "%");
    $("#documentoOficial").css("left", "inherit");
}

function pesquisaFavoritos() {
    var searchText = $('#inputPesquisaFavoritos').val().toLowerCase();

    $('li.itemFavorito').filter(function () {
        return !($(this).text().toLowerCase().indexOf(searchText) != -1);
    }).css('display', 'none');

    $('li.itemFavorito').filter(function () {
        return ($(this).text().toLowerCase().indexOf(searchText) != -1);
    }).css('display', 'block');
}

function mostraEscondeFiltrosUnidades() {
    if ($('li.itemOrcamentario').length >= 5) {
        $('#filtroPesquisaOrcamentaria').css('display', '');
    }
    if ($('li.itemAdministrativo').length >= 5) {
        $('#filtroPesquisaAdministrativa').css('display', '');
    }
    controlaTamanhoLista();
}

function mostraEscondeFiltrosFavoritos() {
    if ($('li.itemFavorito').length > 5) {
        $('#filtroPesquisaFavoritos').css('display', '');
    }
    controlaTamanhoLista();
}

function copiaBreadCrump() {
    $("#dd-header-0").appendTo("#copiabreadcrumb");

    $("#dd-header-0 > ul > li").filter(function () {
        $("#copiabreadcrumb").append("<li><span class='divide'>&nbsp;&rsaquo;&rsaquo;&nbsp;</span></li>");
        $("#copiabreadcrumb").append($(this));
    }).css('display', 'none');

}

function mantemNoDrop() {
    $('.mantemNoDrop').click(function (e) {
        e.stopPropagation();
    });
    $(document).on("click", ".ui-datepicker", function (e) {
        e.stopPropagation();
    });
    mostraEscondeFiltrosUnidades();
    mostraEscondeFiltrosFavoritos();
}

function controlaTamanhoLista() {
    if ($('.listaDeUnidades').height() > 500) {
        $(".listaDeUnidades").css('max-height', '500px');
        $(".listaDeUnidades").css('overflow', 'auto');
    }
}

function imprimeDocumentoOficialDireto() {
    $(PrimeFaces.escapeClientId('FormularioDocumentoOficial:areaDocumentoOficial')).jqprint();
    return false;
    ;
    ;
}

var posicaoInicial = null;
var idComponente = null;

function reposicionarPanelResultadoAutoCompleteQuandoPaginaGrande(source) {
    var elem = document.getElementById(source + '_panel');
    if (source != idComponente) {
        posicaoInicial = null;
        idComponente = source;
    }
    if (posicaoInicial == null) {
        posicaoInicial = elem.style.top;
    }
    elem.style.top = posicaoInicial;
}

function porcentagem2(v) {
    if (parseInt(v.toString()) > parseInt('100')) {
        v = v.replace(v, '100');
    }

    v = porcentagem(v);


    v = v.replace(/([\d])([\d]{1,2})$/, "$1,$2")
    v = valor + "%"
    return v
}

function start() {
    aguarde.show();
}

function stop() {
    aguarde.hide();
    $('#spinner').fadeOut("slow");
}

function desabilitarBotaoEnter(e) {
    if (e.keyCode == 13) {
        e.preventDefault();
        return;
    }
}

function getRequestContextPath() {
    var base = document.getElementById('base');
    return base.value;
}

function getUrlNotificacaoService() {
    var base = document.getElementById('urlNotificacaoService');
    return base.value;
}


function cancelarAcaoBotaoEnter(event) {
    if (event.keyCode == 13) {
        event.preventDefault();
        return;
    }
}


function impŕimirNoWord(component) {

    var preHtml = "<html xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:w='urn:schemas-microsoft-com:office:word' xmlns='http://www.w3.org/TR/REC-html40'><head><meta charset='utf-8'><title>Export HTML To Doc</title></head><body>";
    var postHtml = "</body></html>";
    var html = preHtml + component.html() + postHtml;

    var blob = new Blob(['\ufeff', html], {
        type: 'application/msword'
    });

    // Specify link url
    var url = 'data:application/vnd.ms-word;charset=utf-8,' + encodeURIComponent(html);

    // Create download link element
    var downloadLink = document.createElement("a");

    document.body.appendChild(downloadLink);

    // Specify file name
    filename = 'document.doc';

    if (navigator.msSaveOrOpenBlob) {
        navigator.msSaveOrOpenBlob(blob, filename);
    } else {
        // Create a link to the file
        downloadLink.href = url;

        // Setting the file name
        downloadLink.download = filename;

        //triggering the function
        downloadLink.click();
    }
    document.body.removeChild(downloadLink);
}

function formatXml(xml) {
    var formatted = '';
    var reg = /(>)(<)(\/*)/g;
    xml = xml.replace(reg, '$1\r\n$2$3');
    var pad = 0;
    jQuery.each(xml.split('\r\n'), function (index, node) {
        var indent = 0;
        if (node.match(/.+<\/\w[^>]*>$/)) {
            indent = 0;
        } else if (node.match(/^<\/\w/)) {
            if (pad != 0) {
                pad -= 1;
            }
        } else if (node.match(/^<\w[^>]*[^\/]>.*$/)) {
            indent = 1;
        } else {
            indent = 0;
        }

        var padding = '';
        for (var i = 0; i < pad; i++) {
            padding += '  ';
        }

        formatted += padding + node + '\r\n';
        pad += indent;
    });

    return formatted;
}

function formatarValorInteiroDashboard(valor) {
    var retorno = valor >= 1000 ?
        valor.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".") :
        valor;
    return retorno;
}

function formatarValorPorcentagemDashboard(valor) {
    var retorno = valor >= 1000 ?
        valor.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".") :
        valor;
    return retorno + "%";
}

function formatarValorRSDashboard(valor) {
    var moeda = 'R$ ';
    valor = ('' + valor).replace(',', '.');
    valor = ('' + valor).split('.');
    var parteInteira = valor[0];
    var parteDecimal = valor[1];

    // tratar a parte inteira
    var rx = /(\d+)(\d{3})/;
    parteInteira = parteInteira.replace(/^\d+/, function (w) {
        while (rx.test(w)) {
            w = w.replace(rx, '$1.$2');
        }
        return w;
    });

    // tratar a parte decimal
    var formatoDecimal = 2;

    if (parteDecimal) parteDecimal = parteDecimal.slice(0, formatoDecimal);
    else if (!parteDecimal && formatoDecimal) {
        parteDecimal = '';
        while (parteDecimal.length < formatoDecimal) {
            parteDecimal = '0' + parteDecimal;
        }
    }
    if (parteDecimal.length < formatoDecimal) {
        while (parteDecimal.length < formatoDecimal) {
            parteDecimal = parteDecimal + '0';
        }
    }
    var string = moeda + (parteDecimal ? [parteInteira, parteDecimal].join(',') : parteInteira);
    return string;
}


function cancelFullScreen(el) {
    var requestMethod = el.cancelFullScreen||el.webkitCancelFullScreen||el.mozCancelFullScreen||el.exitFullscreen;
    if (requestMethod) { // cancel full screen.
        requestMethod.call(el);
    } else if (typeof window.ActiveXObject !== "undefined") { // Older IE.
        var wscript = new ActiveXObject("WScript.Shell");
        if (wscript !== null) {
            wscript.SendKeys("{F11}");
        }
    }
}

function requestFullScreen(el) {
    // Supports most browsers and their versions.
    var requestMethod = el.requestFullScreen || el.webkitRequestFullScreen || el.mozRequestFullScreen || el.msRequestFullscreen;

    if (requestMethod) { // Native full screen.
        requestMethod.call(el);
    } else if (typeof window.ActiveXObject !== "undefined") { // Older IE.
        var wscript = new ActiveXObject("WScript.Shell");
        if (wscript !== null) {
            wscript.SendKeys("{F11}");
        }
    }
    return false
}

function toggleFull() {
    var elem = document.body; // Make the body go full screen.
    var isInFullScreen = (document.fullScreenElement && document.fullScreenElement !== null) ||  (document.mozFullScreen || document.webkitIsFullScreen);

    if (isInFullScreen) {
        cancelFullScreen(document);
    } else {
        requestFullScreen(elem);
    }
    return false;
}

function openFullscreen(elem) {
    if (elem.requestFullscreen) {
        elem.requestFullscreen();
    } else if (elem.mozRequestFullScreen) { /* Firefox */
        elem.mozRequestFullScreen();
    } else if (elem.webkitRequestFullscreen) { /* Chrome, Safari and Opera */
        elem.webkitRequestFullscreen();
    } else if (elem.msRequestFullscreen) { /* IE/Edge */
        elem.msRequestFullscreen();
    }
}

function closeFullscreen(elem) {
    if (document.exitFullscreen) {
        document.exitFullscreen();
    } else if (document.mozCancelFullScreen) { /* Firefox */
        document.mozCancelFullScreen();
    } else if (document.webkitExitFullscreen) { /* Chrome, Safari and Opera */
        document.webkitExitFullscreen();
    } else if (document.msExitFullscreen) { /* IE/Edge */
        document.msExitFullscreen();
    }
}

function moveCursorToEnd(elem) {
    if (typeof elem.selectionStart == "number") {
        elem.selectionStart = elem.selectionEnd = elem.value.length;
    } else if (typeof elem.createTextRange != "undefined") {
        elem.focus();
        var range = elem.createTextRange();
        range.collapse(false);
        range.select();
    }
}

function cpfCnpj(v) {

    // Remove tudo o que não é dígito
    v = v.replace(/\D/g, "");
    if (v.length == 11) { // CPF

        // Coloca um ponto entre o terceiro e o quarto dígitos
        v = v.replace(/(\d{3})(\d)/, "$1.$2");

        // Coloca um ponto entre o terceiro e o quarto dígitos
        // de novo (para o segundo bloco de números)
        v = v.replace(/(\d{3})(\d)/, "$1.$2");

        // Coloca um hífen entre o terceiro e o quarto dígitos
        v = v.replace(/(\d{3})(\d{1,2})$/, "$1-$2");

    } else { // CNPJ

        // Coloca ponto entre o segundo e o terceiro dígitos
        v = v.replace(/^(\d{2})(\d)/, "$1.$2");

        // Coloca ponto entre o quinto e o sexto dígitos
        v = v.replace(/^(\d{2})\.(\d{3})(\d)/, "$1.$2.$3");

        // Coloca uma barra entre o oitavo e o nono dígitos
        v = v.replace(/\.(\d{3})(\d)/, ".$1/$2");

        // Coloca um hífen depois do bloco de quatro dígitos
        v = v.replace(/(\d{4})(\d)/, "$1-$2");

    }
    return v
}

function openDialog(id) {
    $(id).modal({backdrop: 'static', keyboard: false});
    $(id).modal('show');
}

function openDialogLarge(id) {
    $(id).modal({backdrop: 'static', keyboard: false});
    $(id).modal('show');
    let wTotal = window.innerWidth;
    var diferencaW = wTotal * 0.2;
    var aplicarW = ((diferencaW / 2) * 100) / wTotal;
    $(id).css("width", (wTotal - diferencaW) + "px");
    $(id).css("right", aplicarW + "%");
    $(id).css("left", "inherit");
}

function closeDialog(id) {
    $(id).modal('hide');
}

function carregarTodasUnidadesMedidasDaTela() {

    const elements = [...document.querySelectorAll('[unidade-medida-tip]')]

    for (const el of elements) {
        var id = el.getAttribute('unidade-medida-tip')
        const tip = document.createElement('div')
        tip.classList.add('tooltip')

        const xmlHttp = new XMLHttpRequest();
        xmlHttp.open("GET", getRequestContextPath() + '/spring/administrativo/unidade-medida/' + id, false);
        xmlHttp.send(null);
        const unidade = JSON.parse(xmlHttp.responseText);

        tip.innerHTML =
        `<div>
        <div class="alert alert-info"  align="center">
            Configuração Máscara Unidade de Medida
        </div>
        <table>
        <tbody style="text-align: left !important;">
        <tr>
        <td><span style="font-weight: bold">Descrição: </span></td>
        <td>` + unidade.descricao + `</td>
        </tr>
        <tr>
        <td><span style="font-weight: bold">Sigla: </span></td>
        <td>` + unidade.sigla + `</td>
        </tr>
        <tr>
        <td><span style="font-weight: bold">Quantidade: </span></td>
        <td>` + unidade.mascaraQuantidadeExemplo + `</td>
        <td>` + unidade.mascaraQuantidadeDescricao + `</td>
        </tr>
        <tr>
        <td><span style="font-weight: bold">Valor Unitário (R$): </span></td>
        <td>` + unidade.mascaraValorExemplo + `</td>
        <td>` + unidade.mascaraValorDescricao + `</td>
        </tr>
        </tbody>
        </table>
        </div>`;

        const x = el.hasAttribute('tip-left') ? 'calc(-100% - 5px)' : '16px'
        const y = el.hasAttribute('tip-top') ? '-100%' : '0'
        tip.style.transform = `translate(${x}, ${y})`
        el.appendChild(tip)
        el.onpointermove = e => {
            if (e.target !== e.currentTarget) return
            const rect = tip.getBoundingClientRect()
            const rectWidth = rect.width + 16
            const vWidth = window.innerWidth - rectWidth
            const rectX = el.hasAttribute('tip-left') ? e.clientX - rectWidth : e.clientX + rectWidth
            const minX = el.hasAttribute('tip-left') ? 0 : rectX
            const maxX = el.hasAttribute('tip-left') ? vWidth : window.innerWidth
            const x = rectX < minX ? rectWidth : rectX > maxX ? vWidth : e.clientX
            tip.style.left = `${x}px`
            tip.style.top = `${e.clientY}px`
        }
    }
}

function aumentarTamanho(id, plusWidth, plusHeight, maxWidth, maxHeight) {
    var width = $(id).width();
    if ((width + plusWidth) <= maxWidth) {
        $(id).width(width + plusWidth);
    }
    var height = $(id).height();
    if ((height + plusHeight) < maxHeight) {
        $(id).height(height + plusHeight);
    }
}

function diminuirTamanho(id, minusWidth, minusHeight, minWidth, minHeight) {
    var width = $(id).width();
    if ((width - minusWidth) >= minWidth) {
        $(id).width(width - minusWidth);
    }
    var height = $(id).height();
    if ((height - minusHeight) >= minHeight) {
        $(id).height(height - minusHeight);
    }
}

function validarTelefone(elem) {
    if (elem.value.toString().length < 14) {
        elem.value = '';
    }
}
