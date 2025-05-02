update eventofp
set AUTOMATICO = 1,
    REGRA = 'var categoriaESocial = prestador.obterCategoriaeSocial();

return categoriaESocial == 711 || categoriaESocial == 712 || categoriaESocial == 734;',
    FORMULA = 'var valorServico = fichaRPA.getValor().doubleValue();

return  valorServico * (prestador.avaliaReferencia(''4122'') / 100);',
    REFERENCIA = 'var categoriaESocial = prestador.obterCategoriaeSocial();

if (categoriaESocial == 711){
return 60.0;
}

if (categoriaESocial == 712 || categoriaESocial == 734){
return 10.0;
}

return 0.0;',
    VALORBASEDECALCULO = 'return fichaRPA.getValor().doubleValue();'
where CODIGO = 4122
