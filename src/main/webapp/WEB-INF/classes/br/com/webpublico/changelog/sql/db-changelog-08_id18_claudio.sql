create or replace
function real_ufm( valor in NUMBER ) return NUMBER 
is ufm_atual NUMBER;
begin  
select 
m.valor into ufm_atual
from moeda m 
inner join indiceeconomico indice on indice.id = m.indiceeconomico_id 
where indice.descricao = 'UFM' and  
m.ano = (select to_number(to_char(sysdate, 'yyyy')) from dual) and 
m.mes = (select to_number(to_char(sysdate, 'MM')) from dual);
return (valor / ufm_atual);
end;