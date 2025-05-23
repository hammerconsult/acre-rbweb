INSERT INTO ARQUIVOREGISTRODEOBITO (id, arquivo_id, registrodeobito_id, certidaodeobito)
select hibernate_sequence.nextval,
       reg.arquivo_id,
       reg.id as registrodeobito_id,
       case when lower(a.NOME) like '%certidão de óbito%' then 1 else 0 end as certidaodeobito
from REGISTRODEOBITO reg
         inner join arquivo a on REG.ARQUIVO_ID = a.ID;

