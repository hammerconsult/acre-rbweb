--FATOR DE SITUAÇÃO DE QUADRA
INSERT INTO EVENTOCALCULO (ID, DESCRICAO, REGRA, TRIBUTO_ID, TIPOLANCAMENTO, CRIADOEM, IDENTIFICACAO,
                           DESCONTOSOBRE_ID, INICIOVIGENCIA, FINALVIGENCIA, SIGLA)
SELECT HIBERNATE_SEQUENCE.nextval,
       'NOVO IPTU - Fator de Situação de Quadra',
       'fatorSituacaoQuadra();
      function fatorSituacaoQuadra() {
        if (typeof atributo_ni_situacao_quadra === ''undefined'') {
          return 0.0;
        }
        return atributo_ni_situacao_quadra.fator;
      }',
       null,
       'CALCULO_COMPLEMENTAR',
       670024201098130,
       'niFatorSituacaoQuadra',
       null,
       current_date,
       null,
       'N.I.F.S.Q'
from dual
where not exists (select 1 from eventocalculo where IDENTIFICACAO = 'niFatorSituacaoQuadra')
