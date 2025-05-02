insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval, 'TRIBUTÁRIO > CADASTRO MUNICIPAL > UNIFICAÇÃO DE CADASTRO ECONÔMICO EM LOTE',
       '/tributario/cadastromunicipal/unificacaocadastroeconomicolote/edita.xhtml', 0, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/cadastromunicipal/unificacaocadastroeconomicolote/edita.xhtml');

insert into gruporecursosistema (GRUPORECURSO_ID, RECURSOSISTEMA_ID)
select gr.id, rs.id
from gruporecurso gr, recursosistema rs
where gr.nome = 'ADMINISTRADOR'
  and rs.caminho = '/tributario/cadastromunicipal/unificacaocadastroeconomicolote/edita.xhtml'
  and not exists (select 1 from gruporecursosistema s
                  where s.GRUPORECURSO_ID = gr.id
                    and s.RECURSOSISTEMA_ID = rs.id);

insert into menu (id, label, caminho, pai_id, ordem)
select hibernate_sequence.nextval, 'UNIFICAÇÃO DE CADASTRO ECONÔMICO EM LOTE',
       '/tributario/cadastromunicipal/unificacaocadastroeconomicolote/edita.xhtml',
       (select id from menu where label = 'CADASTRO TÉCNICO MOBILIÁRIO - C.M.C.'),
       (select max(ordem) from menu where pai_id = (select s.id from menu s where s.label = 'CADASTRO TÉCNICO MOBILIÁRIO - C.M.C.')) from dual
where not exists (select 1 from menu where caminho = '/tributario/cadastromunicipal/unificacaocadastroeconomicolote/edita.xhtml');
