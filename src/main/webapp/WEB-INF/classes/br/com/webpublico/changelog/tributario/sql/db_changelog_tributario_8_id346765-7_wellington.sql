INSERT INTO EVENTOCALCULO (ID, DESCRICAO, REGRA, TRIBUTO_ID, TIPOLANCAMENTO,
                           CRIADOEM, IDENTIFICACAO, DESCONTOSOBRE_ID, INICIOVIGENCIA,
                           FINALVIGENCIA, SIGLA)
SELECT HIBERNATE_SEQUENCE.nextval, 'Imunidade de I.P.T.U',
       '(cadastro.imune != null && cadastro.imune) || (patrimonio != null && patrimonio != 1) ? 1.0 : 0.0',
       null, 'CALCULO_COMPLEMENTAR', 3196567376702148, 'imunidadeIptu', null,
       DATE '1980-01-01', null, 'IMU I.P.T.'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM EVENTOCALCULO WHERE DESCRICAO = 'Imunidade de I.P.T.U')
