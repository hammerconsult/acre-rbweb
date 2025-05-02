update lotereducaovalorbem set tipolancamento = 'ESTORNO'
where situacaolotereducaovalorbem = 'ESTORNO';

update lotereducaovalorbem set tipolancamento = 'NORMAL'
where situacaolotereducaovalorbem = 'NORMAL';

update lotereducaovalorbem set situacao = 'CONCLUIDA'
