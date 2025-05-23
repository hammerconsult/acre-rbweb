insert into PESSOAHORARIOFUNCIONAMENTO (ID, PESSOA_ID, HORARIOFUNCIONAMENTO_ID, ATIVO)
select hibernate_sequence.nextval, pc.PESSOA_ID, pc.HORARIOFUNCIONAMENTO_ID, 1 from pessoacnae pc
inner join cnae cnae on cnae.id = pc.CNAE_ID
where pc.tipo = 'Primaria'
 and cnae.SITUACAO = 'ATIVO'
 and pc.HORARIOFUNCIONAMENTO_ID is not null
