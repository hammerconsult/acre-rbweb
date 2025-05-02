update parcelavalordivida set DIVIDAATIVAAJUIZADA = 1 where id in (
select pvd.id from PROCESSOJUDICIAL pj
inner join PROCESSOJUDICIALCDA pjCda on pjCda.PROCESSOJUDICIAL_ID = pj.id
inner join CERTIDAODIVIDAATIVA cda on cda.id = pjCda.CERTIDAODIVIDAATIVA_ID
inner join exercicio ex on ex.id = cda.exercicio_id
inner join ITEMCERTIDAODIVIDAATIVA icda on icda.CERTIDAO_ID = cda.id
inner join ITEMINSCRICAODIVIDAATIVA iida on iida.id = icda.ITEMINSCRICAODIVIDAATIVA_ID
inner join valordivida vd on vd.calculo_id = iida.id
inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id
where coalesce(pvd.DIVIDAATIVAAJUIZADA,0) = 0
)
