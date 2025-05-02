update calculoitbi set SITUACAO = 'ABERTO' where id in (
select itbi.id from calculoitbi itbi
where itbi.situacao = 'PAGO'
  and exists (select pvd.id from parcelavalordivida pvd
              inner join situacaoparcelavalordivida spvd on spvd.id = pvd.SITUACAOATUAL_ID
              inner join valordivida vd on vd.id = pvd.VALORDIVIDA_ID
              where spvd.SITUACAOPARCELA = 'EM_ABERTO'
                and vd.calculo_id = itbi.id)
)
