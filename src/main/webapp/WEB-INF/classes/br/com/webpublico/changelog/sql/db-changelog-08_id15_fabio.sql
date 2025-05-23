delete from grupousuariopermissoes;
update recursosistema set caminho = replace(caminho, '/faces/', '/');
delete from recursosistema where caminho in (select caminho from recursosistema group by caminho having count(caminho) > 1) and not exists (select * from gruporecursosistema where recursosistema_id = recursosistema.id);
