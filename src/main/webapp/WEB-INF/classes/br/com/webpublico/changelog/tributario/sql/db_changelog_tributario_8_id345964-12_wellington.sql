--VALOR VENAL DO TERRENO
INSERT INTO EVENTOCALCULO (ID, DESCRICAO, REGRA, TRIBUTO_ID, TIPOLANCAMENTO, CRIADOEM, IDENTIFICACAO,
                           DESCONTOSOBRE_ID, INICIOVIGENCIA, FINALVIGENCIA, SIGLA)
SELECT HIBERNATE_SEQUENCE.nextval,
       'NOVO IPTU - Valor Venal do Terreno',
       'calculador.executaEvento(''niAreaTotalTerreno'') *
        calculador.executaEvento(''niValorMetroQuadradoLoteReferencia'') *
          calculador.executaEvento(''niFatorSituacaoQuadra'') *
            calculador.executaEvento(''niFatorTopografia'') *
              calculador.executaEvento(''niFatorPedologia'') *
                calculador.executaEvento(''niFatorArea'')',
       null,
       'CALCULO_COMPLEMENTAR',
       612876305332108,
       'niValorVenalTerreno',
       null,
       current_date,
       null,
       'N.I.V.V.T'
from dual
where not exists (select 1 from eventocalculo where IDENTIFICACAO = 'niValorVenalTerreno');

--AREA TOTAL DO TERRENO
INSERT INTO EVENTOCALCULO (ID, DESCRICAO, REGRA, TRIBUTO_ID, TIPOLANCAMENTO, CRIADOEM, IDENTIFICACAO,
                           DESCONTOSOBRE_ID, INICIOVIGENCIA, FINALVIGENCIA, SIGLA)
SELECT HIBERNATE_SEQUENCE.nextval,
       'NOVO IPTU - Área Total do Terreno',
       'calculador.areaConstruidaDoLote()',
       null,
       'CALCULO_COMPLEMENTAR',
       669784861390706,
       'niAreaTotalTerreno',
       null,
       current_date,
       null,
       'N.I.A.T.T'
from dual
where not exists (select 1 from eventocalculo where IDENTIFICACAO = 'niAreaTotalTerreno');

--VALOR VENAL DA CONSTRUÇÃO
INSERT INTO EVENTOCALCULO (ID, DESCRICAO, REGRA, TRIBUTO_ID, TIPOLANCAMENTO, CRIADOEM, IDENTIFICACAO,
                           DESCONTOSOBRE_ID, INICIOVIGENCIA, FINALVIGENCIA, SIGLA)
SELECT HIBERNATE_SEQUENCE.nextval,
       'NOVO IPTU - Valor Venal da Construção',
       'calculador.executaEvento(''niAreaConstrucao'') *
        calculador.executaEvento(''niValorMetroQuadradoConstrucao'') *
          calculador.executaEvento(''niFatorIdadeAparente'') *
            calculador.executaEvento(''niFatorEstadoConservacao'')',
       null,
       'CALCULO_COMPLEMENTAR',
       607493875204122,
       'niValorVenalConstrucao',
       null,
       current_date,
       null,
       'N.I.V.V.C'
from dual
where not exists (select 1 from eventocalculo where IDENTIFICACAO = 'niValorVenalConstrucao');



--AREA DA CONSTRUÇÃO
INSERT INTO EVENTOCALCULO (ID, DESCRICAO, REGRA, TRIBUTO_ID, TIPOLANCAMENTO, CRIADOEM, IDENTIFICACAO,
                           DESCONTOSOBRE_ID, INICIOVIGENCIA, FINALVIGENCIA, SIGLA)
SELECT HIBERNATE_SEQUENCE.nextval,
       'NOVO IPTU - Área da Construção',
       'construcao != null && construcao.areaConstruida != null ? construcao.areaConstruida : 0.0',
       null,
       'CALCULO_COMPLEMENTAR',
       607880062868907,
       'niAreaConstrucao',
       null,
       current_date,
       null,
       'N.I.A.C'
from dual
where not exists (select 1 from eventocalculo where IDENTIFICACAO = 'niAreaConstrucao');

--VALOR DO METRO QUADRADO DA CONSTRUÇÃO
INSERT INTO EVENTOCALCULO (ID, DESCRICAO, REGRA, TRIBUTO_ID, TIPOLANCAMENTO, CRIADOEM, IDENTIFICACAO,
                           DESCONTOSOBRE_ID, INICIOVIGENCIA, FINALVIGENCIA, SIGLA)
SELECT HIBERNATE_SEQUENCE.nextval,
       'NOVO IPTU - Valor do Metro Quadrado da Construção',
       'calculador.arredondaValor(Number(calculador.pontuacaoConstrucao(ni_pontuacao_tipologia_construtiva)) * calculador.ufmrb())',
       null,
       'CALCULO_COMPLEMENTAR',
       585309958109546,
       'niValorMetroQuadradoConstrucao',
       null,
       current_date,
       null,
       'N.I.V.M.C'
from dual
where not exists (select 1 from eventocalculo where IDENTIFICACAO = 'niValorMetroQuadradoConstrucao');

--FATOR DE IDADE APARENTE
INSERT INTO EVENTOCALCULO (ID, DESCRICAO, REGRA, TRIBUTO_ID, TIPOLANCAMENTO, CRIADOEM, IDENTIFICACAO,
                           DESCONTOSOBRE_ID, INICIOVIGENCIA, FINALVIGENCIA, SIGLA)
SELECT HIBERNATE_SEQUENCE.nextval,
       'NOVO IPTU - Fator de Idade Aparente',
       'Number(calculador.pontuacaoConstrucao(ni_pontuacao_fator_idade))',
       null,
       'CALCULO_COMPLEMENTAR',
       610309812437108,
       'niFatorIdadeAparente',
       null,
       current_date,
       null,
       'N.I.F.I.A'
from dual
where not exists (select 1 from eventocalculo where IDENTIFICACAO = 'niFatorIdadeAparente');

--FATOR DO ESTADO DE CONSERVAÇÃO
INSERT INTO EVENTOCALCULO (ID, DESCRICAO, REGRA, TRIBUTO_ID, TIPOLANCAMENTO, CRIADOEM, IDENTIFICACAO,
                           DESCONTOSOBRE_ID, INICIOVIGENCIA, FINALVIGENCIA, SIGLA)
SELECT HIBERNATE_SEQUENCE.nextval,
       'NOVO IPTU - Fator do Estado de Conservação',
       'atributo_ni_conservacao != null && atributo_ni_conservacao.fator != null ? Number(atributo_ni_conservacao.fator) : 0.0',
       null,
       'CALCULO_COMPLEMENTAR',
       611014691838002,
       'niFatorEstadoConservacao',
       null,
       current_date,
       null,
       'N.I.F.E.C'
from dual
where not exists (select 1 from eventocalculo where IDENTIFICACAO = 'niFatorEstadoConservacao');

--VALOR VENAL DO IMÓVEL
INSERT INTO EVENTOCALCULO (ID, DESCRICAO, REGRA, TRIBUTO_ID, TIPOLANCAMENTO, CRIADOEM, IDENTIFICACAO,
                           DESCONTOSOBRE_ID, INICIOVIGENCIA, FINALVIGENCIA, SIGLA)
SELECT HIBERNATE_SEQUENCE.nextval,
       'NOVO IPTU - Valor Venal do Imóvel',
       'calculador.executaEvento(''niValorVenalTerreno'') +
        calculador.executaEvento(''niValorVenalConstrucoes'')',
       null,
       'CALCULO_COMPLEMENTAR',
       613056261463812,
       'niValorVenalImovel',
       null,
       current_date,
       null,
       'N.I.V.V.I'
from dual
where not exists (select 1 from eventocalculo where IDENTIFICACAO = 'niValorVenalImovel');

--IPTU
INSERT INTO EVENTOCALCULO (ID, DESCRICAO, REGRA, TRIBUTO_ID, TIPOLANCAMENTO, CRIADOEM, IDENTIFICACAO,
                           DESCONTOSOBRE_ID, INICIOVIGENCIA, FINALVIGENCIA, SIGLA)
SELECT HIBERNATE_SEQUENCE.nextval,
       'NOVO IPTU - IPTU',
       'calculador.executaEvento(''niValorVenalImovel'') * calculador.executaEvento(''niAliquota'')',
       6194076,
       'IMPOSTO',
       613181156660575,
       'niIptu',
       null,
       current_date,
       null,
       'N.I.I'
from dual
where not exists (select 1 from eventocalculo where IDENTIFICACAO = 'niIptu')
