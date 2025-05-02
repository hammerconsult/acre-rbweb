insert into EVENTOFP(ID, CODIGO, COMPLEMENTOREFERENCIA, DESCRICAO, FORMULA, FORMULAVALORINTEGRAL, REFERENCIA, REGRA,
                     TIPOEVENTOFP, AUTOMATICO, UNIDADEREFERENCIA, TIPOEXECUCAOEP, VALORBASEDECALCULO, DESCRICAOREDUZIDA,
                     ATIVO, QUANTIFICACAO, CALCULORETROATIVO, VERBAFIXA, NAOPERMITELANCAMENTO, TIPOCALCULO13,
                     ESTORNOFERIAS,
                     ORDEMPROCESSAMENTO, CONSIGNACAO, DATAREGISTRO, ARREDONDARVALOR, PROPORCIONALIZADIASTRAB,
                     VALORMAXIMOLANCAMENTO)
values (HIBERNATE_SEQUENCE.nextval, 4154, '%', 'INSS EMPRESA PRESTAÇÃO SERVIÇO NORMAL',
        'var base = prestador.calculaBase(''50010'');

var ref = calculador.obterReferenciaValorFP(''11'');

return (base * ref.valor) / 100;',
        'var base = prestador.calculaBase(''50010'');

var ref = calculador.obterReferenciaValorFP(''11'');

return (base * ref.valor) / 100;',
        'return calculador.obterReferenciaValorFP(''11'').valor;',
        'var categoriaESocial = prestador.obterCategoriaeSocial();

return categoriaESocial == 701 || categoriaESocial == 738 || categoriaESocial == 741 || categoriaESocial == 751 || categoriaESocial == 761 || categoriaESocial == 771 || categoriaESocial == 781 || categoriaESocial == 902 || categoriaESocial == 731;',
        'INFORMATIVO', 1, '', 'RPA', 'return prestador.calculaBase(''50010'');', 'INSS EMP PRE SERV NOR', 1, 0, 0, 0, 0, 'NAO',
        0, 4154, 0, sysdate, 0, 1, 0)
