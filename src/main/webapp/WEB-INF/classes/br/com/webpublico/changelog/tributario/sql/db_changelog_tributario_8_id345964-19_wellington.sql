INSERT INTO EVENTOCALCULO (ID, DESCRICAO, REGRA, TRIBUTO_ID, TIPOLANCAMENTO, CRIADOEM, IDENTIFICACAO,
                           DESCONTOSOBRE_ID, INICIOVIGENCIA, FINALVIGENCIA, SIGLA)
SELECT HIBERNATE_SEQUENCE.NEXTVAL,
       'NOVO IPTU - Alíquota',
       'aliquota();
      function aliquota() {
        var predial = !construcao.dummy;
        var valorVenalImovel = calculador.executaEvento(''niValorVenalImovel'');
        if (predial) {
          if (valorVenalImovel <= 50000) {
             return 0.006;
          } else if (valorVenalImovel <= 150000) {
             return 0.0007;
          } else if (valorVenalImovel <= 300000) {
             return 0.0007;
          } else if (valorVenalImovel <= 600000) {
             return 0.0007;
          } else if (valorVenalImovel <= 1200000) {
             return 0.0007;
          } else if (valorVenalImovel <= 3000000) {
             return 0.0007;
          } else if (valorVenalImovel <= 6000000) {
             return 0.0007;
          } else {
             return 0.01;
          }
        } else {
          if (valorVenalImovel <= 50000) {
            return 0.006;
          } else if (valorVenalImovel <= 150000) {
            return 0.0007;
          } else if (valorVenalImovel <= 300000) {
            return 0.0007;
          } else if (valorVenalImovel <= 600000) {
            return 0.0007;
          } else if (valorVenalImovel <= 1200000) {
            return 0.0007;
          } else if (valorVenalImovel <= 3000000) {
            return 0.0007;
          } else if (valorVenalImovel <= 6000000) {
            return 0.0007;
          } else {
            return 0.01;
          }
        }
      }
      ',
       null,
       'CALCULO_COMPLEMENTAR',
       751978609848526.00,
       'niAliquota',
       null,
       DATE '2024-01-01',
       null,
       'N.I.A'
from dual
where not exists (select 1 from eventocalculo where identificacao = 'niAliquota')
