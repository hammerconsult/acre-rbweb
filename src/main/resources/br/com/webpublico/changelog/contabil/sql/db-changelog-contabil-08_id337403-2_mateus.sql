update PLANODECONTAS pc set TIPOCONTA_ID =
(select tc.id
 from tipoconta tc
  inner join tipoconta tcPc on tcPc.id = pc.TIPOCONTA_ID
 where tc.EXERCICIO_ID = pc.EXERCICIO_ID
       and tcpc.CLASSEDACONTA = tc.CLASSEDACONTA
       and tcpc.DESCRICAO = tc.descricao
       and tc.mascara = tcpc.mascara
)
