update cidade
set codigosiafi = '0139'
where uf_id = (select id from uf where uf = 'AC')
  and upper(nome) = 'RIO BRANCO'
