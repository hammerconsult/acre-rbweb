INSERT INTO PAGINAMENUHPAGINAPREF (ID, NOME, TIPOATOLEGAL, PAGINAMENUHORIZONTALPORTAL_ID, PAGINAPREFEITURAPORTAL_ID)
SELECT
    HIBERNATE_SEQUENCE.NEXTVAL,
    'Código Municipal',
    'CODIGO_MUNICIPAL',
    (SELECT MENUH.ID FROM PAGINAMENUHORIZPORTAL MENUH WHERE MENUH.INICIOVIGENCIA = TO_DATE('2024-04-01', 'YYYY-MM-DD')),
    (SELECT PREF.ID FROM PAGINAPREFEITURAPORTAL PREF WHERE PREF.CHAVE = 'ato-legal')
FROM dual ;
