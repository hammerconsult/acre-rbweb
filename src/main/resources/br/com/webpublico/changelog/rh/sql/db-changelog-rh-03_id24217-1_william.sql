delete GRUPORECURSOSISTEMA where RECURSOSISTEMA_ID = 736426096;
delete GRUPORECURSOSISTEMA where RECURSOSISTEMA_ID = 737090441;

insert into GRUPORECURSOSISTEMA (GRUPORECURSO_ID, recursosistema_id)
select distinct gruporecurso.GRUPORECURSO_ID, 736426096
from USUARIOSISTEMA usuario
inner join GRUPOUSUARIOSISTEMA grupoUsuario  on grupoUsuario.USUARIOSISTEMA_ID =usuario.ID
inner join ItemGrupoUsuario item on item.GRUPOUSUARIO_ID = grupoUsuario.GRUPOUSUARIO_ID
inner join GRUPORECURSO grupo on grupo.id = item.GRUPORECURSO_ID
inner join GRUPORECURSOSISTEMA gruporecurso on gruporecurso.GRUPORECURSO_ID = grupo.id;

insert into GRUPORECURSOSISTEMA (GRUPORECURSO_ID, recursosistema_id)
select distinct gruporecurso.GRUPORECURSO_ID, 737090441
from USUARIOSISTEMA usuario
inner join GRUPOUSUARIOSISTEMA grupoUsuario  on grupoUsuario.USUARIOSISTEMA_ID =usuario.ID
inner join ItemGrupoUsuario item on item.GRUPOUSUARIO_ID = grupoUsuario.GRUPOUSUARIO_ID
inner join GRUPORECURSO grupo on grupo.id = item.GRUPORECURSO_ID
inner join GRUPORECURSOSISTEMA gruporecurso on gruporecurso.GRUPORECURSO_ID = grupo.id ;
