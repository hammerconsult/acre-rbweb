INSERT INTO CONFIGURACAONFSEPARAMETROS (ID, TIPOPARAMETRO, VALOR, CONFIGURACAO_ID)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'PERMITE_ISENCAO_ACRESCIMOS_INSTITUICAO_FINANCEIRA', 'FALSE', (SELECT ID FROM CONFIGURACAONFSE WHERE ROWNUM = 1));
