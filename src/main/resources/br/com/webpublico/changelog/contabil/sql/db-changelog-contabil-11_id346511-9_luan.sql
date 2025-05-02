insert into PAGINAMENUHPAGINAPREF
values (HIBERNATE_SEQUENCE.NEXTVAL, 'Institucional',
        (select pmh.id from PAGINAMENUHORIZPORTAL pmh where pmh.tipo = 'PRINCIPAL'), null, null, 1, null, null, null);
