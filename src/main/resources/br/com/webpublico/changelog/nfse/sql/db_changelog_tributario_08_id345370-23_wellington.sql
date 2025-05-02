insert into RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > CONFIGURAÇÃO DE DÍVIDA > LISTAR',
       '/tributario/configuracaodivida/lista.xhtml', 0,
       'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where CAMINHO = '/tributario/configuracaodivida/lista.xhtml');

insert into RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > CONFIGURAÇÃO DE DÍVIDA > EDITAR',
       '/tributario/configuracaodivida/edita.xhtml', 0,
       'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where CAMINHO = '/tributario/configuracaodivida/edita.xhtml');

insert into RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > CONFIGURAÇÃO DE DÍVIDA > VISUALIZAR',
       '/tributario/configuracaodivida/visualizar.xhtml', 0,
       'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where CAMINHO = '/tributario/configuracaodivida/visualizar.xhtml');

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
select HIBERNATE_SEQUENCE.nextval, 'CONFIGURAÇÃO DE DÍVIDA',
       '/tributario/configuracaodivida/lista.xhtml',
       (select id from menu where label = 'COMPOSIÇÃO DE DÍVIDA'),
       100 from dual
where not exists (select 1 from menu where CAMINHO = '/tributario/configuracaodivida/lista.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where gr.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and rs.caminho like '/tributario/configuracaodivida/%'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.GRUPORECURSO_ID = gr.ID
                    and grs.RECURSOSISTEMA_ID = rs.id);
