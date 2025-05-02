INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
select hibernate_sequence.nextval, 'GERENCIAMENTO DE RECURSOS > MENSAGEM A TODOS USUÁRIOS > LISTAR',
       '/admin/mensagemtodosusuarios/lista.xhtml', 0, 'CONFIGURACAO' from dual
where not exists (select 1 from RECURSOSISTEMA where CAMINHO = '/admin/mensagemtodosusuarios/lista.xhtml');

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/admin/mensagemtodosusuarios/lista.xhtml'),
       (select id from gruporecurso where nome = 'Gerenciamento de Recursos') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/admin/mensagemtodosusuarios/lista.xhtml')
                    and gruporecurso_id = (select id from gruporecurso where nome = 'Gerenciamento de Recursos'));
