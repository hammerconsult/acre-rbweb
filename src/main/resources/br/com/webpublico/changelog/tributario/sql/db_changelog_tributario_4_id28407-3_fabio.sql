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
    (SELECT ID FROM
      (SELECT distinct iptu.id,
        vd.emissao
      FROM calculoiptu iptu
      INNER JOIN processocalculo PROC ON proc.id = iptu.processocalculoiptu_id
      INNER JOIN valordivida vd ON vd.calculo_id = iptu.id
      inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id
      inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id
      WHERE iptu.cadastroimobiliario_id = PARAM_CADASTRO_ID
        AND proc.exercicio_id = PARAM_EXERCICIO_ID
        and spvd.SITUACAOPARCELA not IN ('CANCELADO_RECALCULO','CANCELADO_ISENCAO','CANCELAMENTO')
      UNION ALL
      SELECT iptu.id,
        vd.emissao
      FROM calculoiptu iptu
      INNER JOIN processocalculo PROC ON proc.id = iptu.processocalculoiptu_id
      INNER JOIN valordivida vd ON vd.calculo_id = iptu.id
      WHERE iptu.cadastroimobiliario_id = PARAM_CADASTRO_ID
      AND proc.exercicio_id = PARAM_EXERCICIO_ID
      and vd.emissao = (SELECT max(vd.emissao)
            FROM calculoiptu iptu
           INNER JOIN calculo ON calculo.id = iptu.id
           INNER JOIN processocalculo PROC ON proc.id = iptu.processocalculoiptu_id
           INNER JOIN valordivida vd ON vd.calculo_id = iptu.id
           WHERE iptu.cadastroimobiliario_id = PARAM_CADASTRO_ID
             AND proc.exercicio_id = PARAM_EXERCICIO_ID)
      )
    ) ULTIMOCALCULO
  WHERE rownum = 1;
  RETURN CALCULO_ID;
END GETULTIMOCALCULOIPTU;
