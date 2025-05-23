insert into alteracaocontratualcont (id, alteracaocontratual_id, contrato_id)
select hibernate_sequence.nextval,
       alt.id,
       alt.contrato_id
from alteracaocontratual alt;
