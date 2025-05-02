begin
for registro in (select acaofiscal_id, usuariosistema_id,
                           count(1), max(id) as max_id
                       from fiscaldesignado
                    group by acaofiscal_id, usuariosistema_id
                    having count(1) > 1)
   loop
delete
from fiscaldesignado fd
where fd.acaofiscal_id = registro.acaofiscal_id
  and fd.usuariosistema_id = registro.usuariosistema_id
  and fd.id != registro.max_id;
end loop;
end;
