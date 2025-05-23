declare
id_usuario number;
begin
for registro in (select id from parametrosaud order by id)
        loop
select us.id into id_usuario
from parametrosaud_aud aud
         inner join revisaoauditoria ra on ra.id = aud.rev
         inner join usuariosistema us on us.login = ra.usuario
where aud.revtype = 0 and aud.id = registro.id;
update parametrosaud set dataregistro = current_date, usuarioregistro_id = id_usuario
where id = registro.id;
end loop;
end;
