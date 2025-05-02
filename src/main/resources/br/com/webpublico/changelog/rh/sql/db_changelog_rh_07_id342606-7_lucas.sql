insert into EVENTOFP(ID, CODIGO, COMPLEMENTOREFERENCIA, DESCRICAO, FORMULA, FORMULAVALORINTEGRAL, REFERENCIA, REGRA,
                     TIPOEVENTOFP, AUTOMATICO, UNIDADEREFERENCIA, TIPOEXECUCAOEP, VALORBASEDECALCULO, DESCRICAOREDUZIDA,
                     ATIVO, QUANTIFICACAO, CALCULORETROATIVO, VERBAFIXA, NAOPERMITELANCAMENTO, TIPOCALCULO13,
                     ESTORNOFERIAS,
                     ORDEMPROCESSAMENTO, CONSIGNACAO, DATAREGISTRO, ARREDONDARVALOR, PROPORCIONALIZADIASTRAB,
                     VALORMAXIMOLANCAMENTO)
values (HIBERNATE_SEQUENCE.nextval, 4072, '%', 'ISS PRESTAÇÃO SERVIÇO TRANSPORTADOR',
        'return (fichaRPA.getValor().doubleValue() * fichaRPA.getPercentualISS().doubleValue()) /100;',
        'return (fichaRPA.getValor().doubleValue() * fichaRPA.getPercentualISS().doubleValue()) /100;',
        'return fichaRPA.getPercentualISS().doubleValue();',
        'var categoriaESocial = prestador.obterCategoriaeSocial();

return fichaRPA.getCobrarISS() && (categoriaESocial == 711 || categoriaESocial == 712 || categoriaESocial == 734);',
        'DESCONTO', 1, null, 'RPA', 'return fichaRPA.getValor().doubleValue();', 'ISS AUT TRANSP', 1, null, 0, 0, 0,
        'NAO', 0, 4072, 1, sysdate,
        1, 1, null)
