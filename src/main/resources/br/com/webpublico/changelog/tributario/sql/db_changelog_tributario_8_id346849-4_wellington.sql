insert into recursosistema (ID, NOME, CAMINHO, CADASTRO, MODULO)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > PARÂMETRO DE COBRANÇA PELO SPC > LISTA',
       '/tributario/parametrocobrancaspc/lista.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/parametrocobrancaspc/lista.xhtml');

insert into recursosistema (ID, NOME, CAMINHO, CADASTRO, MODULO)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > PARÂMETRO DE COBRANÇA PELO SPC > EDITA',
       '/tributario/parametrocobrancaspc/edita.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/parametrocobrancaspc/edita.xhtml');

insert into recursosistema (ID, NOME, CAMINHO, CADASTRO, MODULO)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > PARÂMETRO DE COBRANÇA PELO SPC > VISUALIZA',
       '/tributario/parametrocobrancaspc/visualizar.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/parametrocobrancaspc/visualizar.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho in ('/tributario/parametrocobrancaspc/lista.xhtml',
                     '/tributario/parametrocobrancaspc/edita.xhtml',
                     '/tributario/parametrocobrancaspc/visualizar.xhtml')
  and gr.nome in ('ADMINISTRADOR TRIBUTÁRIO')
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
select HIBERNATE_SEQUENCE.nextval, 'PARÂMETRO DE COBRANÇA PELO SPC', '/tributario/parametrocobrancaspc/lista.xhtml',
       -126758315,
       119 from dual
where not exists (select 1 from menu where label = 'PARÂMETRO DE COBRANÇA PELO SPC');
