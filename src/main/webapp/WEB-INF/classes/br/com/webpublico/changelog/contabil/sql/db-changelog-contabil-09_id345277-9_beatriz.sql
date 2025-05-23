INSERT INTO PAGINARELEVANTEPORTAL (ID, INICIOVIGENCIA, FIMVIGENCIA)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, TO_DATE('2024-04-01', 'YYYY-MM-DD'), NULL);

INSERT INTO PAGINARELEVPAGINAPREF (ID, NOME, PAGINARELEVANTEPORTAL_ID, PAGINAPREFEITURAPORTAL_ID)
SELECT
    HIBERNATE_SEQUENCE.NEXTVAL,
    'Covid-19',
    (SELECT REL.ID FROM PAGINARELEVANTEPORTAL REL WHERE REL.INICIOVIGENCIA = TO_DATE('2024-04-01', 'YYYY-MM-DD')),
    (SELECT PREF.ID FROM PAGINAPREFEITURAPORTAL PREF WHERE PREF.CHAVE = 'covid-19')
FROM dual ;

INSERT INTO PAGINARELEVPAGINAPREF (ID, NOME, PAGINARELEVANTEPORTAL_ID, PAGINAPREFEITURAPORTAL_ID)
SELECT
    HIBERNATE_SEQUENCE.NEXTVAL,
    'Obras',
    (SELECT REL.ID FROM PAGINARELEVANTEPORTAL REL WHERE REL.INICIOVIGENCIA = TO_DATE('2024-04-01', 'YYYY-MM-DD')),
    (SELECT PREF.ID FROM PAGINAPREFEITURAPORTAL PREF WHERE PREF.CHAVE = 'obra')
FROM dual ;

INSERT INTO PAGINARELEVPAGINAPREF (ID, NOME, PAGINARELEVANTEPORTAL_ID, PAGINAPREFEITURAPORTAL_ID)
SELECT
    HIBERNATE_SEQUENCE.NEXTVAL,
    'Termos de Cooperação',
    (SELECT REL.ID FROM PAGINARELEVANTEPORTAL REL WHERE REL.INICIOVIGENCIA = TO_DATE('2024-04-01', 'YYYY-MM-DD')),
    (SELECT PREF.ID FROM PAGINAPREFEITURAPORTAL PREF WHERE PREF.CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/controle-interno-e-contabilidade/termo-de-cooperacao-tecnica-entre-o-tce-e-o-municipio-de-rio-branco/')
FROM dual ;

INSERT INTO PAGINARELEVPAGINAPREF (ID, NOME, PAGINARELEVANTEPORTAL_ID, PAGINAPREFEITURAPORTAL_ID)
SELECT
    HIBERNATE_SEQUENCE.NEXTVAL,
    'Calamidade Pública',
    (SELECT REL.ID FROM PAGINARELEVANTEPORTAL REL WHERE REL.INICIOVIGENCIA = TO_DATE('2024-04-01', 'YYYY-MM-DD')),
    (SELECT PREF.ID FROM PAGINAPREFEITURAPORTAL PREF WHERE PREF.CHAVE = 'calamidade-publica')
FROM dual ;

INSERT INTO PAGINARELEVPAGINAPREF (ID, NOME, PAGINARELEVANTEPORTAL_ID, PAGINAPREFEITURAPORTAL_ID)
SELECT
    HIBERNATE_SEQUENCE.NEXTVAL,
    'Bens Patrimoniais',
    (SELECT REL.ID FROM PAGINARELEVANTEPORTAL REL WHERE REL.INICIOVIGENCIA = TO_DATE('2024-04-01', 'YYYY-MM-DD')),
    (SELECT PREF.ID FROM PAGINAPREFEITURAPORTAL PREF WHERE PREF.CHAVE = 'bem')
FROM dual ;

INSERT INTO PAGINARELEVPAGINAPREF (ID, NOME, PAGINARELEVANTEPORTAL_ID, PAGINAPREFEITURAPORTAL_ID)
SELECT
    HIBERNATE_SEQUENCE.NEXTVAL,
    NULL,
    (SELECT REL.ID FROM PAGINARELEVANTEPORTAL REL WHERE REL.INICIOVIGENCIA = TO_DATE('2024-04-01', 'YYYY-MM-DD')),
    (SELECT PREF.ID FROM PAGINAPREFEITURAPORTAL PREF WHERE PREF.CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/links-uteis/')
FROM dual ;
