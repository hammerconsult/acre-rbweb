insert into ConfiguracaoWebService values (hibernate_sequence.nextval, 'PROCURADORIA', 'TESTE', 'http://200.96.187.43:8080/cdawsWeb/services/ServicoCDA', 'SEFINRBR', '1', null, (select id from configuracaotributario)) ;
insert into ConfiguracaoWebService values (hibernate_sequence.nextval, 'PROCURADORIA', 'PREPRODUCAO', 'http://192.168.86.10:8080/cdawsWeb/services/ServicoCDA', 'SEFINRBR', '1', null, (select id from configuracaotributario)) ;
insert into ConfiguracaoWebService values (hibernate_sequence.nextval, 'PROCURADORIA', 'WPPRODUCAO', 'http://192.168.86.13/cdawsWeb/services/ServicoCDA', 'SEFINRBR', 'saj03pgmrb', null, (select id from configuracaotributario)) ;
insert into ConfiguracaoWebService values (hibernate_sequence.nextval, 'ARQUIVO_CDA', 'WPPRODUCAO', '192.168.86.11', null, null, '/EventosSEFAZ/Envio/', (select id from configuracaotributario)) ;
insert into ConfiguracaoWebService values (hibernate_sequence.nextval, 'ARQUIVO_CDA', 'TESTE', '127.0.0.1', null, null, '/EventosSEFAZ/Envio/', (select id from configuracaotributario)) ;
insert into ConfiguracaoWebService values (hibernate_sequence.nextval, 'EMAIL', 'TESTE', 'smtp.gmail.com', 'suporte@webpublico.com.br', 'suportemga', '465', (select id from configuracaotributario)) ;
insert into ConfiguracaoWebService values (hibernate_sequence.nextval, 'EMAIL', 'PREPRODUCAO', 'smtp.gmail.com', 'suporte@webpublico.com.br', 'suportemga', '465', (select id from configuracaotributario)) ;
insert into ConfiguracaoWebService values (hibernate_sequence.nextval, 'EMAIL', 'WPPRODUCAO', 'smtp.gmail.com', 'suporte@webpublico.com.br', 'suportemga', '465', (select id from configuracaotributario) );
insert into ConfiguracaoWebService values (hibernate_sequence.nextval, 'EMAIL', 'RH', 'smtp.gmail.com', 'suporte@webpublico.com.br', 'suportemga', '465', (select id from configuracaotributario)) ;
insert into ConfiguracaoWebService values (hibernate_sequence.nextval, 'EMAIL', 'HOMOLOGACAO', 'smtp.gmail.com', 'suporte@webpublico.com.br', 'suportemga', '465', (select id from configuracaotributario)) ;
insert into ConfiguracaoWebService values (hibernate_sequence.nextval, 'NFSE', 'TESTE', 'http://homologa.e-nfs.com.br/riobranco/servlet/abaixaarrecadacao', null, null, null, (select id from configuracaotributario) );
insert into ConfiguracaoWebService values (hibernate_sequence.nextval, 'NFSE', 'PREPRODUCAO', 'http://homologa.e-nfs.com.br/riobranco/servlet/abaixaarrecadacao', null, null, null, (select id from configuracaotributario)) ;
insert into ConfiguracaoWebService values (hibernate_sequence.nextval, 'NFSE', 'WPPRODUCAO', 'http://www.e-nfs.com.br/riobranco/servlet/abaixaarrecadacao', null, null, null, (select id from configuracaotributario)) ;


