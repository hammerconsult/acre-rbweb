insert into PAGINAMENUHPAGINAPREF
values (HIBERNATE_SEQUENCE.NEXTVAL, 'Convênios e Transferências',
        (select pmh.id from PAGINAMENUHORIZPORTAL pmh where pmh.tipo = 'PRINCIPAL'),
        null, null, 7, null, null, null)
