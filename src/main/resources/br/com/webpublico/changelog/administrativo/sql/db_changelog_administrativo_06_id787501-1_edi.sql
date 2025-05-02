merge into material o
using (
select
mat.id as id,
(select id from grupomaterial where codigo = '01.16') as idGrupo
from material mat
inner join grupomaterial gb on gb.id = mat.grupo_id
where gb.codigo = '01'
) ob on (ob.id = o.id)
when matched then update set o.grupo_id = ob.idGrupo;
