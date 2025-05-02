update SAIDAMATERIAL
set SITUACAOSAIDAMATERIAL = 'CONCLUIDA', DATACONCLUSAO = DATASAIDA
where ID in (select sm.id
             from SAIDAMATERIAL sm
               inner join TIPOBAIXABENS tbb on tbb.ID = sm.TIPOBAIXABENS_ID
             where tbb.TIPOINGRESSOBAIXA = 'CONSUMO')
