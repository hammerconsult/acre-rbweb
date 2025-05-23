--FATOR DE PEDOLOGIA
INSERT INTO EVENTOCALCULO (ID, DESCRICAO, REGRA, TRIBUTO_ID, TIPOLANCAMENTO, CRIADOEM, IDENTIFICACAO,
                           DESCONTOSOBRE_ID, INICIOVIGENCIA, FINALVIGENCIA, SIGLA)
SELECT HIBERNATE_SEQUENCE.nextval,
       'NOVO IPTU - Fator de Pedologia',
       'fatorPedologia();
      function fatorPedologia() {
        if (typeof atributo_ni_pedologia === ''undefined'') {
            return 0.0;
        }
        return atributo_ni_pedologia.fator;
      }
      ',
       null,
       'CALCULO_COMPLEMENTAR',
       670103982274973,
       'niFatorPedologia',
       null,
       current_date,
       null,
       'N.I.F.P'
from dual
where not exists (select 1 from eventocalculo where IDENTIFICACAO = 'niFatorPedologia')
