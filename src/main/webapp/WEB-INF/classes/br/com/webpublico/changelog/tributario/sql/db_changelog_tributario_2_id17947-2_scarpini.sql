create or replace FUNCTION GETULTIMOVALORDIVIDA(
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
      SELECT VD.id,
        vd.emissao
      FROM calculoiptu iptu
      INNER JOIN processocalculo PROC
      ON proc.id = iptu.processocalculoiptu_id
      INNER JOIN valordivida vd
      ON vd.calculo_id                  = iptu.id
      WHERE iptu.cadastroimobiliario_id = PARAM_CADASTRO_ID
      AND proc.exercicio_id             = PARAM_EXERCICIO_ID
      )
    ORDER BY emissao DESC, id desc
    ) ULTIMOCALCULO
  WHERE rownum = 1;
  RETURN CALCULO_ID;
END GETULTIMOVALORDIVIDA;
