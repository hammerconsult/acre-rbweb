create or replace
FUNCTION GETPROXIMONUMERODAM(EXERCICIO_ID in NUMBER)
return number
AS
NUMERO NUMBER;
BEGIN
SELECT COALESCE(MAX(NUMERO),0)+1 INTO NUMERO FROM DAM WHERE DAM.EXERCICIO_ID = EXERCICIO_ID and dam.numero < 500000000;
return numero;
END GETPROXIMONUMERODAM;