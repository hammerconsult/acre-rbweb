insert into EVENTOFP(ID, CODIGO, COMPLEMENTOREFERENCIA, DESCRICAO, FORMULA, FORMULAVALORINTEGRAL, REFERENCIA, REGRA,
                     TIPOEVENTOFP, AUTOMATICO, UNIDADEREFERENCIA, TIPOEXECUCAOEP, VALORBASEDECALCULO, DESCRICAOREDUZIDA,
                     ATIVO, QUANTIFICACAO, CALCULORETROATIVO, VERBAFIXA, NAOPERMITELANCAMENTO, TIPOCALCULO13,
                     ESTORNOFERIAS,
                     ORDEMPROCESSAMENTO, CONSIGNACAO, DATAREGISTRO, ARREDONDARVALOR, PROPORCIONALIZADIASTRAB,
                     VALORMAXIMOLANCAMENTO)
values (HIBERNATE_SEQUENCE.nextval, 4772, null, 'REDUTOR BASE INSS TRANSPORTADOR', 'var base = prestador.calculaBase(''60000'');

return base * (prestador.avaliaReferencia(''4772'') / 100);', 'return 0;', 'return 80.0;', 'var categoriaESocial = prestador.obterCategoriaeSocial();

return categoriaESocial == 711 || categoriaESocial == 712 || categoriaESocial == 734;', 'INFORMATIVO', 1, null, 'RPA',
        'return prestador.calculaBase(''60000'');', 'RED BS INSS TRANSP', 1, null, 0, 0, 0, 'NAO', 0, 4772, 0, sysdate,
        1, 1, null)
