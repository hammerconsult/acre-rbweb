update basefp set FILTROBASEFP = 'PENSAO_ALIMENTICIA'  where id in (select id from basefp where id IN(select basefp_id from ValorPensaoAlimenticia) and codigo >= 2000)
