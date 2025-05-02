insert into responsavelservicoartrrt (id, responsavelservico_id, artrrt)
select hibernate_sequence.nextval, r.id, r.artrrt
from responsavelservico r
where not exists(select 1
                 from responsavelservicoartrrt s
                 where s.responsavelservico_id = r.id
                   and s.artrrt = r.artrrt)
