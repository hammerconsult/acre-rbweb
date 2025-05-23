insert into INCIDENCIAACRESCIMOMULTA (id, multa_id, incidencia)
select hibernate_sequence.nextval, multa.id, inc.INCIDENCIA from incidenciaacrescimo inc
inner join multaconfiguracaoacrescimo multa on multa.CONFIGURACAOACRESCIMOS_ID = inc.CONFIGURACAOACRESCIMOS_ID
where inc.TIPOACRESCIMO = 'MULTA'
and not exists (select id from INCIDENCIAACRESCIMOMULTA where multa_id = multa.id)
