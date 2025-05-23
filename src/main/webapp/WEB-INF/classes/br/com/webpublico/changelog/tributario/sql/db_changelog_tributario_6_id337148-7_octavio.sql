merge into enderecocalculoalvara endereco_antigo
using (select eca.id as endereco_id, vw.logradouro, vw.bairro, vw.cep
       from enderecocalculoalvara eca
                inner join processocalculoalvara pca on eca.processocalculoalvara_id = pca.id
                inner join cadastroeconomico cmc on pca.cadastroeconomico_id = cmc.id
                inner join vwenderecopessoa vw on cmc.pessoa_id = vw.pessoa_id
       where pca.situacaocalculoalvara <> 'ESTORNADO'
) endereco_novo
on (endereco_antigo.id = endereco_novo.endereco_id)
when matched then update set
                             endereco_antigo.logradouro = endereco_novo.logradouro,
                             endereco_antigo.bairro = endereco_novo.bairro,
                             endereco_antigo.cep = endereco_novo.cep;
