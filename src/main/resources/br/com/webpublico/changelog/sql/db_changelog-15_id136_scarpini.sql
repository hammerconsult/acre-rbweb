  CREATE OR REPLACE FORCE VIEW VWCONSULTADEDEBITOS ("CADASTRO_ID", "PESSOA_ID", "DIVIDA_ID", "CALCULO_ID", "VALORDIVIDA_ID", "PARCELA_ID", "SITUACAOPARCELA_ID", "SITUACAOPARCELA", "EXERCICIO_ID", "OPCAOPAGAMENTO_ID", "VALORORIGINAL", "VALORCORRIGIDO", "VALORJUROS", "VALORMULTA", "VALORHONORARIOS", "VALORIMPOSTO", "VALORTAXA", "VALORDESCONTO", "PARCELA", "PROMOCIONAL") AS
  SELECT DISTINCT
  cadastro.id           AS CADASTRO_ID,
  pessoa.id             AS PESSOA_ID,
  divida.id             AS DIVIDA_ID,
  calculo.id            AS CALCULO_ID,
  vd.id                 AS VALORDIVIDA_ID,
  pvd.id                AS PARCELA_ID,
  spvd.id               AS SITUACAOPARCELA_ID,
  spvd.situacaoparcela  AS SITUACAOPARCELA,
  exercicio.id          AS EXERCICIO_ID,
  pvd.opcaopagamento_id AS OPCAOPAGAMENTO_ID,
  spvd.saldo            AS VALORORIGINAL,
  pacote_consulta_de_debitos.getvalorcorrecao(spvd.saldo, pvd.id, pvd.dataregistro, exercicio.ano) AS VALORCORRECAO,
  pacote_consulta_de_debitos.getvalorjuros(pvd.vencimento, sysdate, divida.id, spvd.saldo, COALESCE(pvd.dividaativa, 0)) AS VALORJUROS,
  pacote_consulta_de_debitos.getvalormulta(pvd.vencimento, sysdate, divida.id, spvd.saldo, COALESCE(pvd.dividaativa, 0)) AS VALORMULTA,
  pacote_consulta_de_debitos.getvalorhonorarios(divida.id, spvd.saldo, COALESCE(pvd.dividaAtivaAjuizada, 0)) as VALORHONORARIOS,
  pacote_consulta_de_debitos.getvalorimposto(pvd.valor, pvd.id, spvd.saldo) AS VALORIMPOSTO,
  pacote_consulta_de_debitos.getvalortaxa(pvd.valor, pvd.id, spvd.saldo) AS  VALORTAXA,
  pacote_consulta_de_debitos.getvalordesconto(pvd.id) AS VALORDESCONTO,
  pacote_consulta_de_debitos.getnumeroparcela(vd.id, op.id, op.promocional,
  pvd.sequenciaparcela)       AS PARCELA,
  COALESCE(op.promocional, 0) AS PROMOCIONAL
FROM
  parcelavalordivida pvd
INNER JOIN opcaopagamento op
ON
  op.id = pvd.opcaopagamento_id
INNER JOIN valordivida vd
ON
  vd.id = pvd.valordivida_id
INNER JOIN calculo calculo
ON
  calculo.id = vd.calculo_id
INNER JOIN divida divida
ON
  divida.id = vd.divida_id
INNER JOIN exercicio exercicio
ON
  exercicio.id = vd.exercicio_id
INNER JOIN situacaoparcelavalordivida spvd
ON
  spvd.parcela_id = pvd.id
LEFT JOIN calculopessoa calculopessoa
ON
  calculopessoa.calculo_id = calculo.id
LEFT JOIN cadastro cadastro
ON
  cadastro.id = calculo.cadastro_id
LEFT JOIN pessoa pessoa
ON
  pessoa.id = calculopessoa.pessoa_id
WHERE
  spvd.id =
  (
    SELECT
      MAX(id)
    FROM
      situacaoparcelavalordivida
    WHERE
      parcela_id = pvd.id
  )
AND
  (
    (
      OP.PROMOCIONAL   = 1
    AND PVD.VENCIMENTO > sysdate
    )
  OR
    (
      COALESCE(OP.PROMOCIONAL, 0) <> 1
    )
  )