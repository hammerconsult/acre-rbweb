delete from PAGINAMENUHPAGINAPREF
where id in (
    select pagina.id
    from PAGINAMENUHORIZPORTAL pmh
             inner join PAGINAMENUHPAGINAPREF pagina on pagina.paginaMenuHorizontalPortal_id = pmh.id
    where pmh.tipo = 'PADRAO');
