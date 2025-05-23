CREATE OR REPLACE FUNCTION GETULTIMOCALCULOIPTU(
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
      (SELECT iptu.id,
        vd.emissao
      FROM calculoiptu iptu
      INNER JOIN processocalculo PROC
      ON proc.id = iptu.processocalculoiptu_id
      INNER JOIN valordivida vd
      ON vd.calculo_id                  = iptu.id
      WHERE iptu.cadastroimobiliario_id = PARAM_CADASTRO_ID
      AND proc.exercicio_id             = PARAM_EXERCICIO_ID
      UNION ALL
      SELECT iptu.id,
        proc.datalancamento AS emissao
      FROM calculoiptu iptu
      INNER JOIN calculo
      ON calculo.id = iptu.id
      INNER JOIN processocalculo PROC
      ON proc.id                        = iptu.processocalculoiptu_id
      WHERE iptu.cadastroimobiliario_id = PARAM_CADASTRO_ID
      AND proc.exercicio_id             = PARAM_EXERCICIO_ID
      AND (SELECT SUM(item.VALORREAL)
        FROM ITEMCALCULOIPTU item
        WHERE item.isento       =1
        AND item.calculoiptu_id = iptu.id) >= calculo.VALORREAL
      )
    ORDER BY emissao DESC
    ) ULTIMOCALCULO
  WHERE rownum = 1;
  RETURN CALCULO_ID;
END GETULTIMOCALCULOIPTU;
