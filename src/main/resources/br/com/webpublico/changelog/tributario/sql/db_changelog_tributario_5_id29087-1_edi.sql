update divida divida set divida.codigo = (select divs.numero from (
select id, codigo, descricao, rownum as numero from (
select id, codigo, descricao from divida order by id)) divs where divida.id = divs.id)
