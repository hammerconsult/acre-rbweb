insert into PAGINAMENUHPAGINAPREF
values (HIBERNATE_SEQUENCE.NEXTVAL, 'Obras',
        (select pmh.id from PAGINAMENUHORIZPORTAL pmh where pmh.tipo = 'PRINCIPAL'),
        null, null, 10, null, null, null)
