INSERT INTO TIPOLEGISLACAO (ID, DESCRICAO, ORDEMEXIBICAO, HABILITAREXIBICAO)
VALUES (hibernate_sequence.nextval, 'DECRETO', 1, 1);
INSERT INTO TIPOLEGISLACAO (ID, DESCRICAO, ORDEMEXIBICAO, HABILITAREXIBICAO)
VALUES (hibernate_sequence.nextval, 'CÓDIGO TRIBUTÁRIO', 2, 1);
INSERT INTO TIPOLEGISLACAO (ID, DESCRICAO, ORDEMEXIBICAO, HABILITAREXIBICAO)
VALUES (hibernate_sequence.nextval, 'LEIS MUNICIPAIS', 3, 1);


INSERT INTO LEGISLACAO (ID,
                        TIPOLEGISLACAO_ID,
                        NOME,
                        ORDEMEXIBICAO,
                        HABILITAREXIBICAO,
                        SUMULA,
                        DATAPUBLICACAO,
                        DETENTORARQUIVOCOMPOSICAO_ID)
VALUES (hibernate_sequence.nextval,
        (select id from TIPOLEGISLACAO where DESCRICAO = 'LEIS MUNICIPAIS'),
        'LEI 471/2017',
        2,
        1,
        'DISPÕE SOBRE O IMPOSTO SOBRE SERVIÇOS DE QUALQUER NATUREZA, DE COMPETÊNCIA DO MUNICÍPIO, E DÁ OUTRAS PROVIDÊNCIAS.',
        TO_DATE('2017-10-05 00:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        null);
INSERT INTO LEGISLACAO (ID,
                        TIPOLEGISLACAO_ID,
                        NOME,
                        ORDEMEXIBICAO,
                        HABILITAREXIBICAO,
                        SUMULA,
                        DATAPUBLICACAO,
                        DETENTORARQUIVOCOMPOSICAO_ID)
VALUES (hibernate_sequence.nextval,
        (select id from TIPOLEGISLACAO where DESCRICAO = 'CÓDIGO TRIBUTÁRIO'),
        'CÓDIGO TRIBUTARIO MUNICIPAL',
        1,
        1,
        'Dispõe sobre o Sistema Tributário do Município de Prado Ferreira e dá outras providências.',
        TO_DATE('2002-12-27 00:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        null);
INSERT INTO LEGISLACAO (ID,
                        TIPOLEGISLACAO_ID,
                        NOME,
                        ORDEMEXIBICAO,
                        HABILITAREXIBICAO,
                        SUMULA,
                        DATAPUBLICACAO,
                        DETENTORARQUIVOCOMPOSICAO_ID)
VALUES (hibernate_sequence.nextval,
        (select id from TIPOLEGISLACAO where DESCRICAO = 'DECRETO'),
        'DECRETO Nº 19/2016',
        3,
        1,
        'Institui o Gerenciamento Eletrônico do ISSQN, a Escrituração Econômico-Fiscal e a Emissão de GUIA de recolhimento por meios eletrônicos; estabelece obrigações acessórias relativas ao ISSQN - Imposto Sobre Serviços de Qualquer Natureza e dá outras providências.',
        TO_DATE('2016-02-28 00:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        null);
