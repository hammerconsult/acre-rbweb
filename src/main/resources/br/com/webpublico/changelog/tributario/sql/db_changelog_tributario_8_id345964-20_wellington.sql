INSERT INTO EVENTOCALCULO (ID, DESCRICAO, REGRA, TRIBUTO_ID, TIPOLANCAMENTO, CRIADOEM, IDENTIFICACAO,
                           DESCONTOSOBRE_ID, INICIOVIGENCIA, FINALVIGENCIA, SIGLA)
SELECT HIBERNATE_SEQUENCE.nextval,
       'NOVO IPTU - Taxa de Iluminação Pública',
       'ni_patrimonio != "1" ? 0.0 : construcao.dummy ? calculador.ufmrb() * 0.1 : 0.0',
       6208254,
       'TAXA',
       760940935883906,
       'niIluminacaoPublica',
       null,
       current_date,
       null,
       'N.I.I.P'
from dual
where not exists (select 1 from eventocalculo where IDENTIFICACAO = 'niIluminacaoPublica')
