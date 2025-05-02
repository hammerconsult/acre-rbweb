update empenho e set e.codigocontatce = (select coalesce(desp.codigotce, replace(desp.codigo, '.', '')) as codigo from empenho emp
inner join conta desp on emp.CONTADESPESA_ID = desp.id
where e.id = emp.id)
