update paginamenuhpaginapref
set nome = 'Contratações'
where paginaprefeituraportal_id = (select id from paginaprefeituraportal where chave = 'contrato' and nome = 'Contratos')
;

update paginamenuhpaginapref
set nome = 'Transferência Voluntária',
    icone = (select icone from paginaprefeituraportal where chave = 'licitacao-outras' and nome = 'Licitações'),
    paginaprefeituraportal_id = (select id from paginaprefeituraportal where chave = 'liberacao-financeira' and nome = 'Repasse')
where paginaprefeituraportal_id = (select id from paginaprefeituraportal where chave = 'licitacao-outras' and nome = 'Licitações')
;

delete from paginamenuhpaginapref where paginaprefeituraportal_id = (select id from paginaprefeituraportal where nome = 'Pessoal' and chave = 'servidor')
;
