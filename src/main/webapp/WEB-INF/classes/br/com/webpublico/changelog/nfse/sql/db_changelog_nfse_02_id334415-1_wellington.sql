insert into CONFIGURACAONFSEPARAMETROS (id, CONFIGURACAO_ID, TIPOPARAMETRO, VALOR)
select hibernate_sequence.nextval, id, 'PERMITE_CANCELAMENTO_FORA_PRAZO',
       'FALSE'
from configuracaonfse
where not exists(select 1 from CONFIGURACAONFSEPARAMETROS where TIPOPARAMETRO = 'PERMITE_CANCELAMENTO_FORA_PRAZO');
insert into CONFIGURACAONFSEPARAMETROS (id, CONFIGURACAO_ID, TIPOPARAMETRO, VALOR)
select hibernate_sequence.nextval, id, 'TEXTO_BLOQUEIO_CANCELAMENTO_FORA_PRAZO',
       'Nos termos do art. 11 do Decreto 2248/2013, o cancelamento de notas fiscais de serviço eletrônicas FORA do PRAZO deve ser requerido por meio de Processo Administrativo'
from CONFIGURACAONFSE
where not exists(select 1 from CONFIGURACAONFSEPARAMETROS where TIPOPARAMETRO = 'TEXTO_BLOQUEIO_CANCELAMENTO_FORA_PRAZO');
