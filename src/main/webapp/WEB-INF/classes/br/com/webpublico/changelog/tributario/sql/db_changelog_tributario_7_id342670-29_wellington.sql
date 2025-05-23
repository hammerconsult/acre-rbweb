INSERT INTO CLASSIFICACAOUSO (ID, CODIGO, CATEGORIA, DESCRICAO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 1, 'UPE - Usos Perigosos',
        'Atividades que representem grandes riscos provocados por explosão, incêndio ou outro sinistro.');
INSERT INTO CLASSIFICACAOUSO (ID, CODIGO, CATEGORIA, DESCRICAO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 2, 'UES - Usos Especiais',
        'Estabelecimentos potencialmente incômodos ou de risco ambiental.');
INSERT INTO CLASSIFICACAOUSO (ID, CODIGO, CATEGORIA, DESCRICAO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 3, 'AERO - Uso Aeroportuário',
        'Espaços destinados a instalação de atividades relacionadas à aeroportos');
INSERT INTO CLASSIFICACAOUSO (ID, CODIGO, CATEGORIA, DESCRICAO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 4, 'IND 1 - Uso Industrial com Risco Ambiental Relevante',
        'Estabelecimentos que representam risco ambiental oriundo de atividades voltadas para industrialização de produtos, estando sujeitos a licenciamento');
INSERT INTO CLASSIFICACAOUSO (ID, CODIGO, CATEGORIA, DESCRICAO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 5, 'IND 2 - Uso Industrial sem Risco Ambiental Relevante',
        'Estabelecimentos de atividades voltadas para industrialização de produtos, em que o órgão ambiental ateste ausência ou quantidade desprezível de poluentes');
INSERT INTO CLASSIFICACAOUSO (ID, CODIGO, CATEGORIA, DESCRICAO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 6, 'PGT 1 - Polo Gerador de Tráfego',
        'Estabelecimentos com trânsito predominante de cargas pesadas');
INSERT INTO CLASSIFICACAOUSO (ID, CODIGO, CATEGORIA, DESCRICAO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 7, 'PGT 2 - Polo Gerador de Tráfego',
        'Estabelecimentos de comércio ou serviços de grande porte que por sua característica atraem um grande número de veículos e pedestres');
INSERT INTO CLASSIFICACAOUSO (ID, CODIGO, CATEGORIA, DESCRICAO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 8, 'PGT 3 - Polo Gerador de Tráfego',
        'Estabelecimentos com grande concentração de pessoas');
INSERT INTO CLASSIFICACAOUSO (ID, CODIGO, CATEGORIA, DESCRICAO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 9, 'GRN - Gerador de Ruído Noturno',
        'Atividades que geram movimento externo, sons ou ruídos no horário compreendido entre 22 e 6 horas');
INSERT INTO CLASSIFICACAOUSO (ID, CODIGO, CATEGORIA, DESCRICAO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 10, 'GRD - Gerador de Ruído Diurno',
        'Atividades que geram sons ou ruídos no horário diurno');
INSERT INTO CLASSIFICACAOUSO (ID, CODIGO, CATEGORIA, DESCRICAO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 11, 'CSI 1 - Comércio, Serviços e Instituições',
        'Estabelecimentos de comércios e serviços com caráter de maior atratividade de público');
INSERT INTO CLASSIFICACAOUSO (ID, CODIGO, CATEGORIA, DESCRICAO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 12, 'CSI 2 - Comércio, Serviços e Instituições',
        'Estabelecimentos de comércio, serviços e instituições, não enquadrados nas categorias anteriores');
INSERT INTO CLASSIFICACAOUSO (ID, CODIGO, CATEGORIA, DESCRICAO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 13, 'AGRO IND - Agroindustrial',
        'Atividades relacionadas ao uso rural, especialmente aquelas voltadas para produção e industrialização dos produtos');
INSERT INTO CLASSIFICACAOUSO (ID, CODIGO, CATEGORIA, DESCRICAO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 14, 'AGF - Agroflorestal',
        'Atividades voltadas ao cultivo da terra, possíveis de serem desenvolvidas no interior da macrozona urbana');
INSERT INTO CLASSIFICACAOUSO (ID, CODIGO, CATEGORIA, DESCRICAO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 15, 'PAPE - Produção Agropecuária e Extrativista',
        'Atividades voltadas à criação de rebanhos de animais, agricultura e extrativismo florestal, possíveis somente na macrozona rural');
