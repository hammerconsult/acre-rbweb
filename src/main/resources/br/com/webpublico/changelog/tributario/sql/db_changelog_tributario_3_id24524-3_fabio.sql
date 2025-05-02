update DescontoItemParcela set origem = 'PARCELAMENTO'
where id in (
select desconto.id from descontoitemparcela desconto
inner join ItemParcelaValorDivida ipvd on ipvd.id = desconto.itemparcelavalordivida_id
inner join ParcelaValorDivida pvd on pvd.id = ipvd.parcelavalordivida_id
inner join ValorDivida vd on vd.id = pvd.valordivida_id
inner join Calculo cal on cal.id = vd.calculo_id
where cal.tipocalculo IN ('PARCELAMENTO','CANCELAMENTO_PARCELAMENTO')
) and origem is null;

update DescontoItemParcela set origem = 'IPTU'
where id in (
select desconto.id from descontoitemparcela desconto
inner join ItemParcelaValorDivida ipvd on ipvd.id = desconto.itemparcelavalordivida_id
inner join ParcelaValorDivida pvd on pvd.id = ipvd.parcelavalordivida_id
inner join ValorDivida vd on vd.id = pvd.valordivida_id
inner join Calculo cal on cal.id = vd.calculo_id
where cal.tipocalculo = 'IPTU'
) and origem is null;

update DescontoItemParcela set origem = 'OUTRO'
where id in (
select desconto.id from DescontoItemParcela desconto where desconto.origem is null
);
