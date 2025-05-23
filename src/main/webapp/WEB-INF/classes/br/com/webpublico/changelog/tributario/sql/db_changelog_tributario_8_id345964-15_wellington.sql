--FATOR DE TOPOGRAFIA
INSERT INTO EVENTOCALCULO (ID, DESCRICAO, REGRA, TRIBUTO_ID, TIPOLANCAMENTO, CRIADOEM, IDENTIFICACAO,
                           DESCONTOSOBRE_ID, INICIOVIGENCIA, FINALVIGENCIA, SIGLA)
SELECT HIBERNATE_SEQUENCE.nextval,
       'NOVO IPTU - Fator de Topografia',
       'fatorTopografia();
      function fatorTopografia() {
        if (typeof atributo_ni_topografia === ''undefined'') {
          return 0.0;
        }
        return atributo_ni_topografia.fator;
      }',
       null,
       'CALCULO_COMPLEMENTAR',
       670072948670508,
       'niFatorTopografia',
       null,
       current_date,
       null,
       'N.I.F.T'
from dual
where not exists (select 1 from eventocalculo where IDENTIFICACAO = 'niFatorTopografia')
