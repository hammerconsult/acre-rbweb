update eventofp
set TIPOCALCULO13 = 'NAO'
where TIPOCALCULO13 <> 'NAO'
  and ATIVO = 1
  and codigo not in ('1100')
  and id not in (
    select id
    from eventofp
    where DESCRICAO like '%13%'
      and TIPOCALCULO13 <> 'NAO'
      and ATIVO = 1)
