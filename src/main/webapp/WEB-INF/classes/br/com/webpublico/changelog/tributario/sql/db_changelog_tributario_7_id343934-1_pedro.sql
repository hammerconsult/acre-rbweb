UPDATE SORTEIONFSE
SET numero = CASE
                 WHEN numero = 7 THEN 1
                 WHEN numero = 8 THEN 2
                 WHEN numero = 9 THEN 3
    END
where CAMPANHA_ID = (SELECT ID FROM CAMPANHANFSE WHERE SYSDATE BETWEEN INICIO AND FIM);
