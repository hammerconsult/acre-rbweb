update eventofp
set AUTOMATICO = 1,
    REGRA = 'var categoriaESocial = prestador.obterCategoriaeSocial();

return categoriaESocial == 711 || categoriaESocial == 712 || categoriaESocial == 734;',
    FORMULA = 'var valorServico = fichaRPA.getValor().doubleValue();
var valor4121 = prestador.avaliaEvento(''4121'');
var valor4122 = prestador.avaliaEvento(''4122'');

return valorServico - valor4121 - valor4122;',
    REFERENCIA = 'return 0.0;',
    VALORBASEDECALCULO = 'return fichaRPA.getValor().doubleValue();'
where CODIGO = 4120
