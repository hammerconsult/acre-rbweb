insert into anexocontrolelicenciamentoambiental (id, controlelicenciamentoambiental_id,
                                                 revisaocontrolelicenciamentoambiental_id,
                                                 arquivo_id,
                                                 mostrarNoPortalContribuinte)
select HIBERNATE_SEQUENCE.nextval,
       controle.id,
       null,
       arq.ID,
       0
from arquivo arq
         inner join arquivocomposicao arqcomp on arq.id = arqcomp.arquivo_id
         inner join detentorarquivocomposicao detarq on arqcomp.detentorarquivocomposicao_id = detarq.id
         inner join controlelicenciamentoambiental controle on detarq.id = controle.detentorarquivocomposicao_id;

insert into anexocontrolelicenciamentoambiental (id, controlelicenciamentoambiental_id,
                                                 revisaocontrolelicenciamentoambiental_id,
                                                 arquivo_id,
                                                 mostrarNoPortalContribuinte)
select HIBERNATE_SEQUENCE.nextval,
       null,
       revisao.id,
       arq.ID,
       0
from arquivo arq
         inner join arquivocomposicao arqcomp on arq.id = arqcomp.arquivo_id
         inner join detentorarquivocomposicao detarq on arqcomp.detentorarquivocomposicao_id = detarq.id
         inner join revisaocontrolelicenciamentoambiental revisao on detarq.id = revisao.detentorarquivocomposicao_id;

CREATE OR REPLACE TYPE lista_numero_19_casas AS TABLE OF NUMBER(19);
