insert into grupoexportacao
values (HIBERNATE_SEQUENCE.nextval, '9', 'Outras Previdências',
        168990746, 'OP', 'MENSAL');

insert into ITEMGRUPOEXPORTACAO
values (HIBERNATE_SEQUENCE.nextval, 'ADICAO',
        (select grupo.id
         from grupoexportacao grupo
                  inner join moduloexportacao modulo on grupo.MODULOEXPORTACAO_ID = modulo.ID
         where grupo.codigo = 9
           AND modulo.CODIGO = 9), null, (select id from eventofp where CODIGO = '891'));
