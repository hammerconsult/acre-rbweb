insert into EVENTOFP(ID, CODIGO, COMPLEMENTOREFERENCIA, DESCRICAO, FORMULA, FORMULAVALORINTEGRAL, REFERENCIA, REGRA,
                     TIPOEVENTOFP, AUTOMATICO, UNIDADEREFERENCIA, TIPOEXECUCAOEP, VALORBASEDECALCULO, DESCRICAOREDUZIDA,
                     ATIVO, QUANTIFICACAO, CALCULORETROATIVO, VERBAFIXA, NAOPERMITELANCAMENTO, TIPOCALCULO13,
                     ESTORNOFERIAS,
                     ORDEMPROCESSAMENTO, CONSIGNACAO, DATAREGISTRO, ARREDONDARVALOR, PROPORCIONALIZADIASTRAB,
                     VALORMAXIMOLANCAMENTO)
values (HIBERNATE_SEQUENCE.nextval, 4174, '%', 'INSS EMPRESA PRESTAÇÃO SERVIÇO TRANSPORTADOR',
        'var base = prestador.calculaBase(''60010'');

var ref = calculador.obterReferenciaValorFP(''11'');

return (base * ref.valor) / 100;',
        'var base = prestador.calculaBase(''60010'');

var ref = calculador.obterReferenciaValorFP(''11'');

return (base * ref.valor) / 100;',
        'return calculador.obterReferenciaValorFP(''11'').valor;',
        'var categoriaESocial = prestador.obterCategoriaeSocial();

return categoriaESocial == 711 || categoriaESocial == 712 || categoriaESocial == 734;',
        'INFORMATIVO',
        1, null, 'RPA', 'return prestador.calculaBase(''60010'');', 'INSS EMP PRE SERV TRANS', 1, null, 0, 0, 0, 'NAO',
        0, 4174, 0, sysdate, 1, 1,
        null)
