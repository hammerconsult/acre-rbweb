merge into situacaoparcelavalordivida spvd
using (select pvd.situacaoatual_id as id_spvd, ex.ano, processo.codigo from parcelavalordivida pvd
       inner join valordivida vd on pvd.valordivida_id = vd.id
       inner join calculo c on vd.calculo_id = c.id
       inner join calculoitbi itbi on c.id = itbi.id
       inner join processocalculoitbi processo on itbi.processocalculoitbi_id = processo.id
       inner join processocalculo pc on c.processocalculo_id = pc.id
       inner join exercicio ex on vd.exercicio_id = ex.id
       where itbi.sequencia is null
) processo
on (spvd.id = processo.id_spvd) when matched then update
set spvd.referencia = 'Exercício: ' || processo.ano || ' Guia: ' || processo.codigo
