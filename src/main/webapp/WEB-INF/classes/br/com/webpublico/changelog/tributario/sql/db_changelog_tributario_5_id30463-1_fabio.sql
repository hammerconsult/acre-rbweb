update parcelavalordivida set DIVIDAATIVAAJUIZADA = 1 where id in (
select distinct pvd.id
from CancelamentoParcelamento canc
inner join ParcelasCancelParcelamento parcCanc on parcCanc.CancelamentoParcelamento_id = canc.id
inner join ValorDivida vd on vd.calculo_id = canc.id
inner join ParcelaValorDivida pvd on pvd.valorDivida_id = vd.id
inner join ParcelaValorDivida pvdOrig on pvdOrig.id = parcCanc.PARCELA_ID
where pvdOrig.DIVIDAATIVAAJUIZADA = 1
  and pvd.DIVIDAATIVAAJUIZADA = 0
  and parcCanc.tipoParcelaCancelamento = 'PARCELAS_ORIGINAIS'
)
