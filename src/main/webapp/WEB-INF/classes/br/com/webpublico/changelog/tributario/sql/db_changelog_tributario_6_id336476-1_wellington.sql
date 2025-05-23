insert into mensagemcontribusuario (id, mensagemcontribuinte_id, usuarioweb_id)
select hibernate_sequence.nextval, mc.id, uw.id
from mensagemcontribuinte mc, usuarioweb uw
where not exists (select 1
                  from mensagemcontribusuario mcu
                  where mcu.mensagemcontribuinte_id = mc.id)
  and uw.activated = 1
