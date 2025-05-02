insert into PESSOAHORARIOFUNCIONAMENTO (ID, PESSOA_ID, HORARIOFUNCIONAMENTO_ID, ATIVO)
select hibernate_sequence.nextval, pc.PESSOA_ID, pc.HORARIOFUNCIONAMENTO_ID, 1 from pessoacnae pc
inner join cnae cnae on cnae.id = pc.CNAE_ID
inner join pessoafisica pf on pf.id = pc.pessoa_id
where pc.tipo = 'Primaria'
 and cnae.SITUACAO = 'ATIVO'
 and pc.HORARIOFUNCIONAMENTO_ID is not null
 and not exists (select phf.id from PESSOAHORARIOFUNCIONAMENTO phf where phf.PESSOA_ID = pf.id and phf.HORARIOFUNCIONAMENTO_ID = pc.HORARIOFUNCIONAMENTO_ID)
