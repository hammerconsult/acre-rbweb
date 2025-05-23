delete from PAGINAMENUHPAGINAPREF
where id in (select pmh.id from PAGINAMENUHORIZPORTAL pmh where pmh.tipo = 'PADRAO');
