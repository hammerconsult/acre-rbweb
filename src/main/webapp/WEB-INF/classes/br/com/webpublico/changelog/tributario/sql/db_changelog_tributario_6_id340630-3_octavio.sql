update parcelaremessaprotesto prp
set numerocda = (select cda.numero || '/' || ex.ano
                 from certidaodividaativa cda
                 inner join itemcertidaodividaativa icda on icda.certidao_id = cda.id
                 inner join valordivida vd on vd.calculo_id = icda.iteminscricaodividaativa_id
                 inner join parcelavalordivida pvd on pvd.valorDivida_id = vd.id
                 inner join exercicio ex on cda.exercicio_id = ex.id
                 where pvd.id = prp.parcelavalordivida_id
                 and cda.situacaocertidaoda <> 'CANCELADA' )
where numerocda is null
