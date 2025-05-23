update EVENTOFP
set COMPLEMENTOREFERENCIA   = 'R$',
    FORMULA                 = 'return fichaRPA.getValor().doubleValue();',
    FORMULAVALORINTEGRAL    = 'return fichaRPA.getValor().doubleValue();',
    REFERENCIA              = 'return Number(0);',
    REGRA                   = 'var categoriaESocial = prestador.obterCategoriaeSocial();

return categoriaESocial == 701 || categoriaESocial == 738 || categoriaESocial == 741 || categoriaESocial == 751 || categoriaESocial == 761 || categoriaESocial == 771 || categoriaESocial == 781 || categoriaESocial == 902 || categoriaESocial == 731;',
    TIPOEVENTOFP            = 'VANTAGEM',
    AUTOMATICO              = 1,
    UNIDADEREFERENCIA       = 'UN',
    TIPOEXECUCAOEP          = 'RPA',
    VALORBASEDECALCULO      = 'return fichaRPA.getValor().doubleValue();',
    DESCRICAOREDUZIDA       ='PREST SERV',
    ATIVO                   = 1,
    QUANTIFICACAO           = 0,
    CALCULORETROATIVO       = 0,
    VERBAFIXA               = 0,
    NAOPERMITELANCAMENTO    = 0,
    TIPOCALCULO13           = 'NAO',
    ESTORNOFERIAS           = 0,
    ORDEMPROCESSAMENTO      = 4000,
    CONSIGNACAO             = 0,
    DATAALTERACAO           = sysdate,
    ARREDONDARVALOR         = 1,
    PROPORCIONALIZADIASTRAB = 0,
    VALORMAXIMOLANCAMENTO   = 0
where CODIGO = 4000
