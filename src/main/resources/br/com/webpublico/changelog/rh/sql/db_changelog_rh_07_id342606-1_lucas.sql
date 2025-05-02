insert into EVENTOFP(ID, CODIGO, COMPLEMENTOREFERENCIA, DESCRICAO, FORMULA, FORMULAVALORINTEGRAL, REFERENCIA, REGRA,
                     TIPOEVENTOFP, AUTOMATICO, UNIDADEREFERENCIA, TIPOEXECUCAOEP, VALORBASEDECALCULO, DESCRICAOREDUZIDA,
                     ATIVO, QUANTIFICACAO, CALCULORETROATIVO, VERBAFIXA, NAOPERMITELANCAMENTO, TIPOCALCULO13,
                     ESTORNOFERIAS,
                     ORDEMPROCESSAMENTO, CONSIGNACAO, DATAREGISTRO, ARREDONDARVALOR, PROPORCIONALIZADIASTRAB,
                     VALORMAXIMOLANCAMENTO)
values (HIBERNATE_SEQUENCE.nextval, 4020, 'R$', 'PRESTAÇÃO SERVIÇO TRANSPORTADOR',
        'return fichaRPA.getValor().doubleValue();',
        'return fichaRPA.getValor().doubleValue();',
        'return Number(0);',
        'var categoriaESocial = prestador.obterCategoriaeSocial();

return categoriaESocial == 711 || categoriaESocial == 712 || categoriaESocial == 734;', 'VANTAGEM', 1, null, 'RPA',
        'return fichaRPA.getValor().doubleValue();', 'PREST SERV', 1, null, 0, 0, 0, 'NAO', 0, 4020, 0, SYSDATE,
        1, 1, null)
