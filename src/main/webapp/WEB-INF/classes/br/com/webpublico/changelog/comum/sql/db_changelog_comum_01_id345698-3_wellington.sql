insert into recursosistema (ID, NOME, CAMINHO, CADASTRO, MODULO)
select HIBERNATE_SEQUENCE.nextval, 'COMUM > CONFIGURAÇÕES DO GOV.BR > LISTA',
       '/comum/configuracao-gov-br/lista.xhtml', 1, 'CONFIGURACAO' from dual
where not exists (select 1 from recursosistema where caminho = '/comum/configuracao-gov-br/lista.xhtml');

insert into recursosistema (ID, NOME, CAMINHO, CADASTRO, MODULO)
select HIBERNATE_SEQUENCE.nextval, 'COMUM > CONFIGURAÇÕES DO GOV.BR > EDITA',
       '/comum/configuracao-gov-br/edita.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where caminho = '/comum/configuracao-gov-br/edita.xhtml');

insert into recursosistema (ID, NOME, CAMINHO, CADASTRO, MODULO)
select HIBERNATE_SEQUENCE.nextval, 'COMUM > CONFIGURAÇÕES DO GOV.BR > VISUALIZA',
       '/comum/configuracao-gov-br/visualizar.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where caminho = '/comum/configuracao-gov-br/visualizar.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/comum/configuracao-gov-br/lista.xhtml'
  and gr.nome = 'ADMINISTRADOR'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/comum/configuracao-gov-br/edita.xhtml'
  and gr.nome = 'ADMINISTRADOR'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/comum/configuracao-gov-br/visualizar.xhtml'
  and gr.nome = 'ADMINISTRADOR'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
select HIBERNATE_SEQUENCE.nextval, 'CONFIGURAÇÕES DO GOV.BR', '/comum/configuracao-gov-br/lista.xhtml',
       (SELECT ID FROM MENU WHERE LABEL = 'CONFIGURAÇÕES'),
       40 from dual
where not exists (select 1 from menu where label = 'CONFIGURAÇÕES DO GOV.BR');
