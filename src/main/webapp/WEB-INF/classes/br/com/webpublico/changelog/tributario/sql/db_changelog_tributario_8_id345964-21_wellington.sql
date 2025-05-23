INSERT INTO EVENTOCALCULO (ID, DESCRICAO, REGRA, TRIBUTO_ID, TIPOLANCAMENTO, CRIADOEM, IDENTIFICACAO, DESCONTOSOBRE_ID,
                           INICIOVIGENCIA, FINALVIGENCIA, SIGLA)
SELECT HIBERNATE_SEQUENCE.NEXTVAL,
       'NOVO IPTU - Taxa de Coleta de Lixo',
       'taxaColetaLixo();

      function taxaResidencial(diario) {
        if (typeof ni_tipologia !== ''undefined'' && ni_tipologia == 3) {
          return 0.0;
        }
        return diario ? calculador.pontuacaoConstrucao(ni_lixo_residencial_diario) :
          calculador.pontuacaoConstrucao(ni_lixo_residencial_alternado);
      }

      function taxaIndustrial(diario) {
        if (diario) {
          return construcao.areaTotalConstruida <= 100 ? 25.0 :
            construcao.areaTotalConstruida > 100 && construcao.areaTotalConstruida <= 350 ? 37.5 :
              construcao.areaTotalConstruida > 350 && construcao.areaTotalConstruida <= 700 ? 50.0 :
                construcao.areaTotalConstruida > 700 ? 75.0 :
                  0.0;
        } else {
          return construcao.areaTotalConstruida <= 100 ? 12.5 :
            construcao.areaTotalConstruida > 100 && construcao.areaTotalConstruida <= 350 ? 18.75 :
              construcao.areaTotalConstruida > 350 && construcao.areaTotalConstruida <= 700 ? 25.0 :
                construcao.areaTotalConstruida > 700 ? 37.5 :
                  0.0;
        }
      }

      function taxaHospitalar(diario) {
         return taxaOutros(diario);
      }

      function taxaInstitucional(diario) {
        if (diario) {
          return construcao.areaTotalConstruida <= 150.0 ? 10.0 :
            construcao.areaTotalConstruida > 150.0 && construcao.areaTotalConstruida <= 500.0 ? 20.0 :
              construcao.areaTotalConstruida > 500.0 ? 30.0 :
                0.0;
        } else {
          return construcao.areaTotalConstruida <= 150.0 ? 5.0 :
            construcao.areaTotalConstruida > 150.0 && construcao.areaTotalConstruida <= 500.0 ? 10.0 :
              construcao.areaTotalConstruida > 500.0 ? 15.0 :
                0.0;
        }
      }

      function taxaOutros(diario) {
        return diario ? 10.0 : 5.0;
      }

      function taxaColetaLixo() {
        if (typeof ni_utilizacao === ''undefined''
            || typeof ni_lixo === ''undefined''
            || ni_lixo == 3) {
          return 0.0;
        }
        var diario = ni_lixo == 2;
        var ufm = 0.0;
        if (ni_utilizacao == 6) {
          ufm = taxaResidencial(diario);
        } else if (ni_utilizacao == 3) {
          ufm = taxaIndustrial(diario);
        } else if (ni_utilizacao == 4) {
          ufm = taxaHospitalar(diario);
        } else if (ni_utilizacao == 5) {
          ufm = taxaInstitucional(diario);
        } else {
          ufm = taxaOutros(diario);
        }
        return ufm * calculador.ufmrb();
      }',
       6208252,
       'TAXA',
       771238603726289,
       'niTaxaColetaLixo',
       null,
       current_date,
       null,
       'N.I.T.C.L'
from dual
where not exists (select 1 from eventocalculo where identificacao = 'niTaxaColetaLixo')
