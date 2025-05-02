--VALOR VENAL DAS CONSTRUÇÕES
INSERT INTO EVENTOCALCULO (ID, DESCRICAO, REGRA, TRIBUTO_ID, TIPOLANCAMENTO, CRIADOEM, IDENTIFICACAO,
                           DESCONTOSOBRE_ID, INICIOVIGENCIA, FINALVIGENCIA, SIGLA)
SELECT HIBERNATE_SEQUENCE.nextval,
       'NOVO IPTU - Valor Venal das Construções',
       'valorVenalConstrucoes();
      function valorVenalConstrucoes() {
          var valorVenalConstrucoes = 0.0;
          var construcoes = calculador.recuperaConstrucaoPorCadastro();
          for (i = 0; i < construcoes.size(); i++) {
             var cons = construcoes.get(i);
             calculador.construcao = cons;
             valorVenalConstrucoes = valorVenalConstrucoes + calculador.executaEvento(''niValorVenalConstrucao'', ''construcao'', cons);
          }
          return valorVenalConstrucoes;
      }',
       null,
       'CALCULO_COMPLEMENTAR',
       612272551825998,
       'niValorVenalConstrucoes',
       null,
       current_date,
       null,
       'N.I.V.V.CS'
from dual
where not exists (select 1 from eventocalculo where IDENTIFICACAO = 'niValorVenalConstrucoes')
