update SITUACAOPARCELAVALORDIVIDA spvd set spvd.REFERENCIA = (

    select distinct  ('Exercício: ' || exercicio.ANO || ' Nota: ' ||
                      case when length(to_char(nfsa.NUMERO)) > 8 then substr(to_char(nfsa.NUMERO) ,5  ) else to_char(nfsa.NUMERO) end  )  as referencia
    from  NFSAVULSA nfsa
              inner join exercicio on nfsa.EXERCICIO_ID = EXERCICIO.ID
              inner join CALCULONFSAVULSA calcnfsa on nfsa.ID = calcnfsa.NFSAVULSA_ID
              inner join valordivida vd on vd.CALCULO_ID = calcnfsa.id
              inner join parcelavalordivida pvd on vd.ID = pvd.VALORDIVIDA_ID
              inner join calculo on calcnfsa.id = CALCULO.ID
    where calculo.TIPOCALCULO = 'NFS_AVULSA'
      and spvd.id = pvd.SITUACAOATUAL_ID

) where spvd.id in (
    select distinct pvd.SITUACAOATUAL_ID
    from  NFSAVULSA nfsa
              inner join CALCULONFSAVULSA calcnfsa on nfsa.ID = calcnfsa.NFSAVULSA_ID
              inner join valordivida vd on vd.CALCULO_ID = calcnfsa.id
              inner join parcelavalordivida pvd on vd.ID = pvd.VALORDIVIDA_ID
              inner join calculo on calcnfsa.id = CALCULO.ID
    where calculo.TIPOCALCULO = 'NFS_AVULSA'
      and pvd.SITUACAOATUAL_ID = spvd.id );
