update saidamaterial set situacaosaidamaterial = 'CONCLUIDA' where id in (select sd.id from SAIDAMATDESINCORPORACAO sd)
