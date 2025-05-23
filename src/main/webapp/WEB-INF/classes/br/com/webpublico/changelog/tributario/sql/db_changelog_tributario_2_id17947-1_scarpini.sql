create or replace FUNCTION GETULTIMOCALCULOIPTU(
    PARAM_CADASTRO_ID  IN NUMBER,
    PARAM_EXERCICIO_ID IN NUMBER)
  RETURN NUMBER
AS
  CALCULO_ID NUMBER;
BEGIN
  SELECT ULTIMOCALCULO.id
  INTO CALCULO_ID
  FROM
    (SELECT ID
    FROM
      (
      SELECT iptu.id,
        proc.datalancamento AS emissao
      FROM calculoiptu iptu
      INNER JOIN calculo
      ON calculo.id = iptu.id
      INNER JOIN processocalculo PROC
      ON proc.id                        = iptu.processocalculoiptu_id
      WHERE iptu.cadastroimobiliario_id = PARAM_CADASTRO_ID
      AND proc.exercicio_id             = PARAM_EXERCICIO_ID

      )
    ORDER BY emissao DESC
    ) ULTIMOCALCULO
  WHERE rownum = 1;
  RETURN CALCULO_ID;
END GETULTIMOCALCULOIPTU;
