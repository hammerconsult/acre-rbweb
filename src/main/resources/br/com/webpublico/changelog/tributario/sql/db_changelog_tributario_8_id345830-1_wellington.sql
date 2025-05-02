insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > ARRECADAÇÃO > RELATÓRIOS - ARRECADAÇÃO > ACOMPANHAMENTO DA RECEITA > RELATÓRIO DE LANÇAMENTO DE DÉBITOS DO EXERCÍCIO',
       '/tributario/relatorio/relacaolancamentodebitosexercicio.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/relatorio/relacaolancamentodebitosexercicio.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where gr.nome = 'TRB - RELATÓRIOS DE LANÇAMENTO DE ARRECADAÇÃO'
  and rs.caminho = '/tributario/relatorio/relacaolancamentodebitosexercicio.xhtml'
  and not exists (select 1 from gruporecursosistema
                  where gruporecurso_id = gr.id
                    and recursosistema_id = rs.id);

insert into menu (id, label, caminho, pai_id, ordem)
select hibernate_sequence.nextval, 'RELATÓRIO DE LANÇAMENTO DE DÉBITOS DO EXERCÍCIO',
       '/tributario/relatorio/relacaolancamentodebitosexercicio.xhtml',
       (select id from menu where label = 'ACOMPANHAMENTO DA RECEITA' fetch first 1 row only),
       (select max(ordem) + 10 from menu where pai_id = (select id from menu where label = 'ACOMPANHAMENTO DA RECEITA' fetch first 1 row only))
from dual
where not exists (select 1 from menu where caminho = '/tributario/relatorio/relacaolancamentodebitosexercicio.xhtml');
