declare numid number;
begin
    select coalesce(configuracaopix_id, hibernate_sequence.nextval) into numid from configuracaodam where codigofebraban = 3646;
    merge into configuracaopix using dual on (id = (select configuracaopix_id from configuracaodam where codigofebraban = 3646))
    when matched then update set urlintegrador = 'http://172.16.0.73:8088', numeroconvenio = '81051', chavepix = '7a60891e-186b-4738-a733-f4e80c07293e'
    when not matched then insert (id, urlintegrador, numeroconvenio, chavepix) values (numid, 'http://172.16.0.73:8088', '81051', '7a60891e-186b-4738-a733-f4e80c07293e');

    update configuracaodam set configuracaopix_id = numid where codigofebraban = 3646;

    select coalesce(configuracaopix_id, hibernate_sequence.nextval) into numid from configuracaodam where codigofebraban = 209;
    merge into configuracaopix using dual on (id = (select configuracaopix_id from configuracaodam where codigofebraban = 209))
    when matched then update set urlintegrador = 'http://172.16.0.73:8088', numeroconvenio = '93567', chavepix = '49c62a0b-90f1-4d5c-8fe2-adfd7fd57f38'
    when not matched then insert (id, urlintegrador, numeroconvenio, chavepix) values (numid, 'http://172.16.0.73:8088', '93567', '49c62a0b-90f1-4d5c-8fe2-adfd7fd57f38');

    update configuracaodam set configuracaopix_id = numid where codigofebraban = 209;
end;
