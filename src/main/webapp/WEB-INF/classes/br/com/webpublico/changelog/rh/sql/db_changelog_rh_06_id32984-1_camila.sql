update GRAUPARENTESCOPENSIONISTA
set DESCRICAO='Filho menor de 18 anos - REVOGADO'
where CODIGO = 4;

insert into GRAUPARENTESCOPENSIONISTA
values (HIBERNATE_SEQUENCE.nextval, (select max(CODIGO) + 1 from grauparentescopensionista),
        'Filho menor de 21 anos', 174969695, 1, 0);
