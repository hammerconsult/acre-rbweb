insert into multaconfiguracaoacrescimo (id, CONFIGURACAOACRESCIMOS_ID, iniciovigencia, somarmultaoriginal)
select hibernate_sequence.nextval, id, to_date('01/01/1980','dd/MM/yyyy'), SOMARMULTAORIGINAL from configuracaoacrescimos
where not exists (select id from multaconfiguracaoacrescimo where multaconfiguracaoacrescimo.configuracaoacrescimos_id = configuracaoacrescimos.id)
