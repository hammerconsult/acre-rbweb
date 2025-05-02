insert into PessoaCNAE (id, cnae_id, pessoa_id, inicio, fim, tipo, dataregistro, horariofuncionamento_id)
select hibernate_sequence.nextval, ec.cnae_id, pf.id, ec.inicio, ec.fim, coalesce(ec.tipo,'Primaria'), ec.dataregistro, ec.horariofuncionamento_id
from EconomicoCNAE ec
inner join cadastroeconomico ce on ce.id = ec.cadastroeconomico_id
inner join pessoafisica pf on pf.id = ce.pessoa_id
where not exists (select pc.id from PessoaCnae pc where pc.pessoa_id = pf.id and pc.cnae_id = ec.cnae_id and coalesce(pc.tipo,'Primaria') = coalesce(ec.tipo,'Primaria'))
