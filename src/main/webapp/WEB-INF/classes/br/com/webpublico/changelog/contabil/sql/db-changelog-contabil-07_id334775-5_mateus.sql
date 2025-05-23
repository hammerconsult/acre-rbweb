merge into CONTAAUXILIARDETALHADA cad using (
select c.id, ex.id as exercicio
from conta c
  inner join contaauxiliardetalhada cd on c.id = cd.id
  inner join exercicio ex on ex.ano = substr(c.codigo, length(c.codigo) - 3, length(c.codigo))
where c.codigo like '99%' and (c.codigo like '%2019' or c.codigo like '%2020')
) dados on (dados.id = cad.id)
when matched then update set cad.exercicioorigem_id = dados.exercicio
