insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > NOTA FISCAL > RELATÓRIOS DA NFS-E > RELATÓRIO DE RECEITA DE EMPRESAS OPTANTES PELO SIMPLES NACIONAL',
       '/tributario/nfse/relatorio/receita-empresa-simples-nacional.xhtml', 0, 'NFSE'
   from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/nfse/relatorio/receita-empresa-simples-nacional.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where gr.nome = 'TRB - NFS-e - Gerencial'
  and rs.caminho = '/tributario/nfse/relatorio/receita-empresa-simples-nacional.xhtml'
  and not exists (select 1 from gruporecursosistema s
                  where s.gruporecurso_id = gr.id
                    and s.recursosistema_id = rs.id);

insert into menu (id, label, caminho, pai_id, ordem)
select hibernate_sequence.nextval,
       'RELATÓRIO DE RECEITA DE EMPRESAS OPTANTES PELO SIMPLES NACIONAL',
       '/tributario/nfse/relatorio/receita-empresa-simples-nacional.xhtml', 892158256, 700
       from dual
where not exists (select 1 from menu where caminho = '/tributario/nfse/relatorio/receita-empresa-simples-nacional.xhtml');
