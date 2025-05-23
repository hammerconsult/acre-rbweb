update DOCUMENTOLICITACAO doc
set doc.ENVIARPNCC = 1
where doc.id in (select distinct dl.id
                 from DocumentoLicitacao dl
                          inner join tipodocumentoanexo tipo on tipo.ID = dl.TIPODOCUMENTOANEXO_ID
                 where dl.id = doc.id
                   and tipo.TIPOANEXOPNCP in ('ATA_REGISTRO_PRECO', 'AVISO_CONTRATACAO_DIRETA', 'EDITAL', 'CONTRATO'));
