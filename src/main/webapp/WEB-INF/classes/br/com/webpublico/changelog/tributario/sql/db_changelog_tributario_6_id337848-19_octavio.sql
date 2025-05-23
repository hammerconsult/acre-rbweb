merge into configuracaopix using dual on (id = (select id from configuracaopix order by id desc fetch first 1 rows only ))
when matched then update set urlintegrador = 'https://integracaopix.rbweb.riobranco.ac.gov.br/'
when not matched then insert (id, urlintegrador, numeroconvenio, chavepix, configuracaotributario_id)
                      values (hibernate_sequence.nextval, 'https://integracaopix.rbweb.riobranco.ac.gov.br/',
                              '81051', '7a60891e-186b-4738-a733-f4e80c07293e',
                              (select id from configuracaotributario ct where ct.vigencia = (select max(vigencia) from configuracaotributario)));
