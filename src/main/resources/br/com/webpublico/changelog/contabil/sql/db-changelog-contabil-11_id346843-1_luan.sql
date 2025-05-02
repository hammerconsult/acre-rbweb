insert into PAGINAMENUHPAGINAPREF
values (HIBERNATE_SEQUENCE.NEXTVAL, 'Estatísticas',
        (select pmh.id from PAGINAMENUHORIZPORTAL pmh where pmh.tipo = 'PRINCIPAL'),
        null, null, 18, null, null, null)
