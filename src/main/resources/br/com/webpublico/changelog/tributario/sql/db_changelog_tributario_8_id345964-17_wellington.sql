--FATOR DE ÁREA
INSERT INTO EVENTOCALCULO (ID, DESCRICAO, REGRA, TRIBUTO_ID, TIPOLANCAMENTO, CRIADOEM, IDENTIFICACAO,
                           DESCONTOSOBRE_ID, INICIOVIGENCIA, FINALVIGENCIA, SIGLA)
SELECT HIBERNATE_SEQUENCE.nextval,
       'NOVO IPTU - Fator de Área',
       'fatorArea();
      function fatorArea() {
         var area = calculador.executaEvento(''niAreaTotalTerreno'');
         if (area <= 200) {
            return 1.05;
         } else if (area <= 250) {
            return 1.03;
         } else if (area <= 450) {
            return 1.0;
         } else if (area <= 600) {
            return 0.95;
         } else if (area <= 750) {
            return 0.9;
         } else if (area <= 1000) {
            return 0.85;
         } else if (area <= 1500) {
            return 0.8;
         } else if (area <= 2000) {
            return 0.75;
         } else if (area <= 3000) {
            return 0.7;
         } else if (area <= 4000) {
            return 0.65;
         } else if (area <= 5000) {
            return 0.6;
         } else if (area <= 7500) {
            return 0.55;
         } else if (area <= 10000) {
            return 0.5;
         } else if (area <= 50000) {
            return 0.45;
         } else if (area <= 100000) {
            return 0.40;
         } else if (area > 100000) {
            return 0.35;
         } else {
            return 0.0;
         }
      }
        ',
       null,
       'CALCULO_COMPLEMENTAR',
       670137843597848,
       'niFatorArea',
       null,
       current_date,
       null,
       'N.I.F.A'
from dual
where not exists (select 1 from eventocalculo where IDENTIFICACAO = 'niFatorArea')
