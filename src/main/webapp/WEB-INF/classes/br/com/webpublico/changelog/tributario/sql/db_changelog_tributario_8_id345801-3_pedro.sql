INSERT INTO PROCESSOLICENCIAMENTOAMBIENTALCNAE (ID, CNAE_ID, PROCESSOLICENCIAMENTOAMBIENTAL_ID)

SELECT HIBERNATE_SEQUENCE.nextval, CNAE_ID, ID
FROM PROCESSOLICENCIAMENTOAMBIENTAL
WHERE CNAE_ID IS NOT NULL
