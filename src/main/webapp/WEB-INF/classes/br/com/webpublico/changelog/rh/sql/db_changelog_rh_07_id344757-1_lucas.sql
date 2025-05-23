update eventofp
set AUTOMATICO = 1,
    REGRA = 'var categoriaESocial = prestador.obterCategoriaeSocial();

return categoriaESocial == 711 || categoriaESocial == 712 || categoriaESocial == 734;',
    FORMULA = 'var valorServico = fichaRPA.getValor().doubleValue();

return  valorServico * (prestador.avaliaReferencia(''4121'') / 100);',
    REFERENCIA = 'return 20.0;',
    VALORBASEDECALCULO = 'return fichaRPA.getValor().doubleValue();'
where CODIGO = 4121
