--Comando de Insert de GrupoRecursoSistema
declare
  NUM number(10);
  cursor c1 is
  select 
 rs.id as idRecurso, rs.modulo, gr.idGrupo, gr.nomeGrupo
from
  recursoSistema rs
  inner join (select 
  id as idGrupo,
  case lower(nome)
    when 'cadastros gerais' then 'CADASTROS'
    when 'contábil' then 'CONTABIL'
    when 'planejamento público' then 'PLANEJAMENTO'
    when 'tributário' then 'TRIBUTARIO'
    when 'recursos humanos' then 'RH'
    when 'protocolo' then 'PROTOCOLO'
    when 'trânsito e transporte (rbtrans)' then 'RBTRANS'
    when 'frota' then 'FROTA'
    when 'materiais (almoxarifado)' then 'MATERIAIS'
    when 'compras (licitações)' then 'LICITACAO'
    when 'gerenciamento de recursos' then 'GERENCIAMENTO'
    when 'patrimônio' then 'PATRIMONIO'
  end as nomeGrupo  
from
  grupoRecurso gr) gr on gr.nomeGrupo = rs.modulo
where
  not exists (select * from grupoRecursoSistema grs where grs.recursoSistema_id = rs.id and grs.grupoRecurso_id = gr.idGrupo);
BEGIN
  for linha in c1
  LOOP
    INSERT INTO grupoRecursoSistema(recursoSistema_ID, grupoRecurso_ID) VALUES (linha.idRecurso, linha.idGrupo);
    commit;
  END LOOP;
  dbms_output.put_line('FIM');
  commit;
END;