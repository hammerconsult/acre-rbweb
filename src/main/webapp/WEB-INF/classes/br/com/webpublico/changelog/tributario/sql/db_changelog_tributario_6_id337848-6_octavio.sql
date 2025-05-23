insert into configuracaopix (id, urlintegrador, numeroconvenio, chavepix, configuracaotributario_id)
values (hibernate_sequence.nextval, 'https://integracaopix.webpublico.riobranco.ac.gov.br',
        '81051', '7a60891e-186b-4738-a733-f4e80c07293e',
        (select id from configuracaotributario ct order by ct.vigencia desc fetch first 1 rows only))
