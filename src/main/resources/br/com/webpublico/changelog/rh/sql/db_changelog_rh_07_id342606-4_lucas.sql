insert into EVENTOFP(ID, CODIGO, COMPLEMENTOREFERENCIA, DESCRICAO, FORMULA, FORMULAVALORINTEGRAL, REFERENCIA, REGRA,
                     TIPOEVENTOFP, AUTOMATICO, UNIDADEREFERENCIA, TIPOEXECUCAOEP, VALORBASEDECALCULO, DESCRICAOREDUZIDA,
                     ATIVO, QUANTIFICACAO, CALCULORETROATIVO, VERBAFIXA, NAOPERMITELANCAMENTO, TIPOCALCULO13,
                     ESTORNOFERIAS,
                     ORDEMPROCESSAMENTO, CONSIGNACAO, DATAREGISTRO, ARREDONDARVALOR, PROPORCIONALIZADIASTRAB,
                     VALORMAXIMOLANCAMENTO)
values (HIBERNATE_SEQUENCE.nextval, 4052, '%', 'ISS PRESTAÇÃO SERVIÇO NORMAL',
        'return (fichaRPA.getValor().doubleValue() * fichaRPA.getPercentualISS().doubleValue()) /100;',
        'return (fichaRPA.getValor().doubleValue() * fichaRPA.getPercentualISS().doubleValue()) /100;',
        'return fichaRPA.getPercentualISS().doubleValue();',
        'var categoriaESocial = prestador.obterCategoriaeSocial();

return fichaRPA.getCobrarISS() && (categoriaESocial == 701 || categoriaESocial == 738 || categoriaESocial == 741 || categoriaESocial == 751 || categoriaESocial == 761 || categoriaESocial == 771 || categoriaESocial == 781 || categoriaESocial == 902 || categoriaESocial == 731);',
        'DESCONTO', 1, '', 'RPA', 'return fichaRPA.getValor().doubleValue();', 'ISS AUT NORMAL',
        1, 0, 0, 0, 0, 'NAO', 0, 4052, 1, sysdate, 1, 1, 0)
